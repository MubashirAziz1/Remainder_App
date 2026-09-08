package com.remainder.app

import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenAlarmActivityTest {

    @Test
    fun launchesWithoutFinishing() {
        val activity = Robolectric.buildActivity(LockScreenAlarmActivity::class.java).setup().get()
        assertFalse(activity.isFinishing)
    }
}
