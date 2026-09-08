package com.remainder.app.notification

import android.content.Intent
import com.remainder.app.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenTapIntentTest {

    @Test
    fun targetsMainActivity() {
        val intent = LockScreenTapIntent.create(RuntimeEnvironment.getApplication())
        assertEquals(MainActivity::class.java.name, intent.component?.className)
    }

    @Test
    fun reusesExistingTask() {
        val intent = LockScreenTapIntent.create(RuntimeEnvironment.getApplication())
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_SINGLE_TOP != 0)
        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_CLEAR_TOP != 0)
    }
}
