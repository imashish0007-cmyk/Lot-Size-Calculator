const CACHE_NAME = 'tradecalc-pwa-v10-safe';
const ASSETS_TO_CACHE = [
  '/',
  '/index.html',
  '/manifest.json',
  '/icon.svg',
  '/icon-192.png',
  '/icon-512.png',
  '/apple-touch-icon.png'
];

// Helper to determine if a response is the AI Studio warmup/error page
function isWarmupResponse(text) {
  if (!text) return false;
  return (
    text.includes('Please wait while your application starts') ||
    text.includes('Starting Server...') ||
    text.includes('Your application failed to start') ||
    text.includes('warmup_start_time') ||
    text.includes('__aistudio_warmup_failed')
  );
}

// Pre-cache on installation
self.addEventListener('install', (event) => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(async (cache) => {
      for (const asset of ASSETS_TO_CACHE) {
        try {
          const res = await fetch(asset, { cache: 'no-cache' });
          if (res && res.status === 200) {
            const text = await res.clone().text();
            // NEVER cache the warmup page!
            if (!isWarmupResponse(text)) {
              await cache.put(asset, res);
            }
          }
        } catch (e) {
          console.warn('SW pre-cache skip:', asset, e);
        }
      }
    })
  );
});

// Clean up old caches on activation and claim clients
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.map(async (key) => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
          // Also audit current cache to remove any warmup pages
          const cache = await caches.open(key);
          const requests = await cache.keys();
          for (const req of requests) {
            const res = await cache.match(req);
            if (res) {
              const text = await res.text();
              if (isWarmupResponse(text)) {
                console.log('Purging bad warmup response from cache for', req.url);
                await cache.delete(req);
              }
            }
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Smart Fetch Strategy
self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  const url = new URL(event.request.url);

  // Ignore internal Google AI Studio control plane and auth bridge requests
  if (url.pathname.startsWith('/__') || url.hostname.includes('aistudio.google.com')) {
    return;
  }

  event.respondWith(
    (async () => {
      const cache = await caches.open(CACHE_NAME);
      const cachedResponse = await cache.match(event.request);

      // Validate cached response is not a corrupt warmup page
      if (cachedResponse) {
        const text = await cachedResponse.clone().text();
        if (!isWarmupResponse(text)) {
          // Valid cache! Return immediately for ultra-fast instant startup
          // Background fetch to refresh cache
          fetch(event.request, { cache: 'no-cache' }).then(async (netRes) => {
            if (netRes && netRes.status === 200) {
              const netText = await netRes.clone().text();
              if (!isWarmupResponse(netText)) {
                await cache.put(event.request, netRes);
              }
            }
          }).catch(() => {
            // Server offline / cold - cached response was already served!
          });
          return cachedResponse;
        } else {
          // Cached item was the warmup page - delete it!
          await cache.delete(event.request);
        }
      }

      // Not in cache (or was invalid) - fetch from network
      try {
        const networkResponse = await fetch(event.request, { cache: 'no-cache' });
        if (networkResponse && networkResponse.status === 200) {
          const text = await networkResponse.clone().text();
          // If network returned the warmup page, do NOT cache it!
          if (!isWarmupResponse(text)) {
            await cache.put(event.request, networkResponse.clone());
            return networkResponse;
          } else {
            // Server is warming up! Check if we have a fallback index.html in cache
            const fallback = await cache.match('/index.html') || await cache.match('/');
            if (fallback) {
              const fbText = await fallback.clone().text();
              if (!isWarmupResponse(fbText)) {
                return fallback;
              }
            }
            return networkResponse;
          }
        }
        return networkResponse;
      } catch (err) {
        // Offline or connection refused - fallback to cached index.html
        if (event.request.mode === 'navigate') {
          const fallback = await cache.match('/index.html') || await cache.match('/');
          if (fallback) {
            const fbText = await fallback.clone().text();
            if (!isWarmupResponse(fbText)) {
              return fallback;
            }
          }
        }
        throw err;
      }
    })()
  );
});
