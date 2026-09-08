package com.remainder.app

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectSdkTest {

    @Test
    fun minSdkSupportsLockScreenApis() {
        assertEquals(27, ProjectSdk.MIN)
    }

    @Test
    fun targetSdkIs36() {
        assertEquals(36, ProjectSdk.TARGET)
    }

    @Test
    fun compileSdkIs37() {
        assertEquals(37, ProjectSdk.COMPILE)
    }
}
