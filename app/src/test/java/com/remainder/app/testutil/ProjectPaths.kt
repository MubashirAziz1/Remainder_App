package com.remainder.app.testutil

import java.io.File

internal object ProjectPaths {
    val appDir: File = File(".").canonicalFile.let { current ->
        if (current.name == "app") current else File(current, "app")
    }
    val rootDir: File = requireNotNull(appDir.parentFile) { "Unable to resolve project root from ${appDir.path}" }
}
