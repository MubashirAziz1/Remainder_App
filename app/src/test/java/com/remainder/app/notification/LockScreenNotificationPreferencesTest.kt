package com.remainder.app.notification

import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LockScreenNotificationPreferencesTest {

    private lateinit var preferences: LockScreenNotificationPreferences

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("remainder_notifications", Context.MODE_PRIVATE).edit().clear().commit()
        preferences = LockScreenNotificationPreferences(context)
    }

    @Test
    fun defaultIsDisabled() {
        assertFalse(preferences.isEnabled())
    }

    @Test
    fun enablingPersists() {
        preferences.setEnabled(true)
        assertTrue(LockScreenNotificationPreferences(RuntimeEnvironment.getApplication()).isEnabled())
    }

    @Test
    fun disablingPersists() {
        preferences.setEnabled(true)
        preferences.setEnabled(false)
        assertFalse(LockScreenNotificationPreferences(RuntimeEnvironment.getApplication()).isEnabled())
    }
}
