package com.remainder.app.scaffold

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class ManifestNotificationPermissionTest {

    private val manifest: String
        get() = ProjectPaths.appDir.resolve("src/main/AndroidManifest.xml").readText()

    @Test
    fun declaresPostNotificationsPermission() {
        assertTrue(
            "AndroidManifest must declare POST_NOTIFICATIONS for Android 13+",
            manifest.contains("android.permission.POST_NOTIFICATIONS")
        )
    }
}
