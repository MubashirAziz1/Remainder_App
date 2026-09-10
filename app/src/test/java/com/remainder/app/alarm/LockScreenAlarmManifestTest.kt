package com.remainder.app.alarm

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class LockScreenAlarmManifestTest {

    private val manifest: String
        get() = ProjectPaths.appDir.resolve("src/main/AndroidManifest.xml").readText()

    @Test
    fun declaresLockScreenAlarmActivityOverKeyguard() {
        assertTrue(
            "AndroidManifest must declare LockScreenAlarmActivity",
            manifest.contains("android:name=\".LockScreenAlarmActivity\"")
        )
        assertTrue(
            "LockScreenAlarmActivity must show over the keyguard",
            manifest.contains("android:showWhenLocked=\"true\"")
        )
        assertTrue(
            "LockScreenAlarmActivity must turn the screen on",
            manifest.contains("android:turnScreenOn=\"true\"")
        )
    }

    @Test
    fun lockScreenAlarmActivityIsNotExported() {
        val activityBlock = Regex(
            """<activity[\s\S]*?android:name="\.LockScreenAlarmActivity"[\s\S]*?/>"""
        ).find(manifest)?.value.orEmpty()
        assertTrue(
            "LockScreenAlarmActivity must not be exported",
            activityBlock.contains("android:exported=\"false\"")
        )
    }

    @Test
    fun declaresClockAlarmPermissionAndQuery() {
        assertTrue(
            "AndroidManifest must request permission to set Clock alarms",
            manifest.contains("android:name=\"com.android.alarm.permission.SET_ALARM\"")
        )
        assertTrue(
            "AndroidManifest must query Clock alarm handlers on Android 11+",
            manifest.contains("android.intent.action.SET_ALARM")
        )
    }
}
