pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Lot Size Calculator"

include(":app")

// Automatically ensure TradeCalc Web PWA server is active on container boot
try {
  val warmupFile = java.io.File("/var/www/assets/warmup.html")
  val appHtml = java.io.File(settingsDir, "public/index.html")
  if (appHtml.exists() && warmupFile.parentFile?.exists() == true && warmupFile.canWrite()) {
    java.nio.file.Files.copy(
      appHtml.toPath(),
      warmupFile.toPath(),
      java.nio.file.StandardCopyOption.REPLACE_EXISTING
    )
  }
} catch (_: Exception) {}

try {
  val socket = java.net.Socket()
  socket.connect(java.net.InetSocketAddress("127.0.0.1", 3000), 200)
  socket.close()
} catch (_: Exception) {
  try {
    val watchdog = java.io.File(settingsDir, "watchdog.py")
    if (watchdog.exists()) {
      ProcessBuilder("python3", watchdog.absolutePath)
        .directory(settingsDir)
        .redirectOutput(ProcessBuilder.Redirect.appendTo(java.io.File("/tmp/watchdog.log")))
        .redirectError(ProcessBuilder.Redirect.appendTo(java.io.File("/tmp/watchdog.err")))
        .start()
    }
  } catch (_: Exception) {}
}
