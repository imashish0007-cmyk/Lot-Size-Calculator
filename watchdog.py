import time
import subprocess
import os
import socket
import shutil

SERVER_JS = "/app/applet/server.js"
INDEX_HTML = "/app/applet/public/index.html"
WARMUP_HTML = "/var/www/assets/warmup.html"
LOG_FILE = "/tmp/node.log"

def is_port_open(port=3000):
    try:
        with socket.create_connection(("127.0.0.1", port), timeout=0.8):
            return True
    except (socket.timeout, ConnectionRefusedError, OSError):
        return False

def sync_warmup():
    try:
        if os.path.exists(INDEX_HTML) and os.path.exists(os.path.dirname(WARMUP_HTML)):
            shutil.copyfile(INDEX_HTML, WARMUP_HTML)
    except Exception as e:
        pass

def start_server():
    try:
        with open(LOG_FILE, "a") as log:
            subprocess.Popen(
                ["node", SERVER_JS],
                stdout=log,
                stderr=log,
                cwd="/app/applet",
                start_new_session=True
            )
            print("Watchdog started TradeCalc server.", flush=True)
    except Exception as e:
        print("Watchdog error starting server:", e, flush=True)

if __name__ == "__main__":
    sync_warmup()
    while True:
        try:
            if not is_port_open(3000):
                start_server()
        except Exception:
            pass
        time.sleep(1.5)
