package com.remainder.app.notification

import android.app.NotificationManager
import com.remainder.app.MainActivity
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityNotificationSyncTest {

    @Test
    fun launchCreatesLockScreenNotificationChannel() {
        Robolectric.buildActivity(MainActivity::class.java).setup()

        val manager = RuntimeEnvironment.getApplication()
            .getSystemService(NotificationManager::class.java)
        assertNotNull(manager.getNotificationChannel(LockScreenNotificationSpec.CHANNEL_ID))
    }
}
