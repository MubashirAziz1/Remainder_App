package com.remainder.app.notification

import android.Manifest
import android.os.Build
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class NotificationPermissionPolicyTest {

    @Test
    @Config(sdk = [33])
    fun android13RequiresRuntimePermission() {
        assertTrue(NotificationPermissionPolicy.requiresRuntimePermission(Build.VERSION.SDK_INT))
    }

    @Test
    @Config(sdk = [27])
    fun preTiramisuDoesNotRequireRuntimePermission() {
        assertFalse(NotificationPermissionPolicy.requiresRuntimePermission(Build.VERSION.SDK_INT))
    }

    @Test
    @Config(sdk = [27])
    fun preTiramisuIsGrantedWithoutAsking() {
        assertTrue(NotificationPermissionPolicy.isGranted(RuntimeEnvironment.getApplication()))
    }

    @Test
    @Config(sdk = [33])
    fun android13IsDeniedByDefault() {
        assertFalse(NotificationPermissionPolicy.isGranted(RuntimeEnvironment.getApplication()))
    }

    @Test
    @Config(sdk = [33])
    fun android13IsGrantedAfterPermission() {
        val context = RuntimeEnvironment.getApplication()
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        assertTrue(NotificationPermissionPolicy.isGranted(context))
    }
}
