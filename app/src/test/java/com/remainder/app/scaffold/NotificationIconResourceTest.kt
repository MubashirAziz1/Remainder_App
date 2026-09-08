package com.remainder.app.scaffold

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationIconResourceTest {

    @Test
    fun statusBarNotificationIconExists() {
        val icon = ProjectPaths.appDir.resolve("src/main/res/drawable/ic_stat_lock_reminder.xml")
        assertTrue("Expected notification status icon at ${icon.path}", icon.isFile)
    }
}
