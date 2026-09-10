package com.remainder.app.alarm

import android.provider.AlarmClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AlarmIntentFactoryTest {

    @Test
    fun createsSetAlarmIntentWithTimeTitleAndSkipUi() {
        val intent = AlarmIntentFactory.create(
            AlarmDraft(hour = 6, minute = 30, title = "Morning run")
        )

        assertEquals(AlarmClock.ACTION_SET_ALARM, intent.action)
        assertEquals(6, intent.getIntExtra(AlarmClock.EXTRA_HOUR, -1))
        assertEquals(30, intent.getIntExtra(AlarmClock.EXTRA_MINUTES, -1))
        assertEquals("Morning run", intent.getStringExtra(AlarmClock.EXTRA_MESSAGE))
        assertTrue(intent.getBooleanExtra(AlarmClock.EXTRA_SKIP_UI, false))
    }
}
