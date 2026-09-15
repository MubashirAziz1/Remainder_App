package com.remainder.app.onboarding

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ContextSystemSettingsLauncherTest {

    @Test
    fun launchFromApplicationContextAddsNewTaskFlag() {
        val context = RuntimeEnvironment.getApplication()

        ContextSystemSettingsLauncher(context).launch(
            SystemSettingsIntentFactory.appDetails("com.remainder.app")
        )

        val started = shadowOf(context).nextStartedActivity
        assertEquals("android.settings.APPLICATION_DETAILS_SETTINGS", started.action)
        assertTrue(
            "Settings launch from application context must carry FLAG_ACTIVITY_NEW_TASK",
            started.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0
        )
        assertEquals(Uri.parse("package:com.remainder.app"), started.data)
    }

    @Test
    fun channelSettingsLaunchCarriesChannelAndPackageExtras() {
        val context = RuntimeEnvironment.getApplication()

        ContextSystemSettingsLauncher(context).launch(
            SystemSettingsIntentFactory.channelNotificationSettings(
                "com.remainder.app",
                "lock_screen_reminder"
            )
        )

        val started = shadowOf(context).nextStartedActivity
        assertEquals("android.settings.CHANNEL_NOTIFICATION_SETTINGS", started.action)
        assertEquals("lock_screen_reminder", started.getStringExtra("android.provider.extra.CHANNEL_ID"))
        assertEquals("com.remainder.app", started.getStringExtra("android.provider.extra.APP_PACKAGE"))
    }
}
