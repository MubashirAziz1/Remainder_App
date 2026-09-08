package com.remainder.app.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class LockScreenNotificationSpecTest {

    @Test
    fun channelIdIsStable() {
        assertEquals("lock_screen_reminder", LockScreenNotificationSpec.CHANNEL_ID)
    }

    @Test
    fun notificationIdIsStable() {
        assertEquals(1001, LockScreenNotificationSpec.NOTIFICATION_ID)
    }

    @Test
    fun channelNameIsUserFacing() {
        assertEquals("Lock-screen reminder", LockScreenNotificationSpec.CHANNEL_NAME)
    }

    @Test
    fun contentTitleIsRemainder() {
        assertEquals("Remainder", LockScreenNotificationSpec.CONTENT_TITLE)
    }

    @Test
    fun contentTextInvitesTap() {
        assertEquals("Tap to open named alarms", LockScreenNotificationSpec.CONTENT_TEXT)
    }
}
