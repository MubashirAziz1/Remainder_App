package com.remainder.app.alarm

import android.provider.AlarmClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmIntentFactoryTest {

    @Test
    fun createsSetAlarmSpecWithTimeTitleAndSkipUi() {
        val spec = AlarmIntentFactory.create(
            AlarmDraft(hour = 6, minute = 30, title = "Morning run")
        )

        assertEquals(AlarmClock.ACTION_SET_ALARM, spec.action)
        assertEquals(6, spec.hour)
        assertEquals(30, spec.minute)
        assertEquals("Morning run", spec.message)
        assertTrue(spec.skipUi)
    }
}
