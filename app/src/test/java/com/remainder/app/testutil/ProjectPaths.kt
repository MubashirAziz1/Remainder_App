package com.remainder.app.testutil

import java.io.File

internal object ProjectPaths {
    val appDir: File = File(".").canonicalFile.let { current ->
        if (current.name == "app") current else File(current, "app")
    }
    val rootDir: File = appDir.parentFile
}
