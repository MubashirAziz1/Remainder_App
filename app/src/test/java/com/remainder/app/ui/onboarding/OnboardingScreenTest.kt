package com.remainder.app.ui.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remainder.app.onboarding.NotificationGuidance
import com.remainder.app.onboarding.SystemSettingsIntentSpec
import com.remainder.app.ui.theme.RemainderTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OnboardingScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(
        needsRuntimeRequest: Boolean = true,
        guidance: NotificationGuidance = NotificationGuidance.None,
        onRequestPermission: () -> Unit = {},
        onOpenSettings: (SystemSettingsIntentSpec) -> Unit = {},
        onCompleted: () -> Unit = {}
    ) {
        composeRule.setContent {
            RemainderTheme {
                OnboardingScreen(
                    packageName = "com.remainder.app",
                    needsRuntimeNotificationRequest = needsRuntimeRequest,
                    notificationGuidance = guidance,
                    onRequestNotificationPermission = onRequestPermission,
                    onOpenSystemSettings = onOpenSettings,
                    onCompleted = onCompleted
                )
            }
        }
    }

    @Test
    fun notificationStepIsShownFirst() {
        setContent()

        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeRule.onNodeWithText("Allow notifications").assertIsDisplayed()
    }

    @Test
    fun allowNotificationsRequestsRuntimePermission() {
        var requested = false
        setContent(onRequestPermission = { requested = true })

        composeRule.onNodeWithText("Allow notifications").performClick()

        assertTrue(requested)
    }

    @Test
    fun allowNotificationsHiddenWhenRuntimeRequestNotNeeded() {
        setContent(needsRuntimeRequest = false)

        composeRule.onNodeWithText("Allow notifications").assertDoesNotExist()
    }

    @Test
    fun notificationStepLinksToAppNotificationSettings() {
        val launched = mutableListOf<SystemSettingsIntentSpec>()
        setContent(onOpenSettings = { launched.add(it) })

        composeRule.onNodeWithText("Open notification settings").performClick()

        assertEquals(1, launched.size)
        assertEquals("android.settings.APP_NOTIFICATION_SETTINGS", launched[0].action)
        assertEquals("com.remainder.app", launched[0].packageName)
    }

    @Test
    fun nextAdvancesToLockScreenStep() {
        setContent()

        composeRule.onNodeWithText("Next").performClick()

        composeRule.onNodeWithText("Lock screen").assertIsDisplayed()
    }

    @Test
    fun lockScreenStepLinksToChannelSettings() {
        val launched = mutableListOf<SystemSettingsIntentSpec>()
        setContent(onOpenSettings = { launched.add(it) })

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Open lock-screen settings").performClick()

        assertEquals(1, launched.size)
        assertEquals("android.settings.CHANNEL_NOTIFICATION_SETTINGS", launched[0].action)
        assertEquals("lock_screen_reminder", launched[0].channelId)
        assertEquals("com.remainder.app", launched[0].packageName)
    }

    @Test
    fun nextTwiceReachesBatteryStepWithDoneAction() {
        setContent()

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()

        composeRule.onNodeWithText("Battery optimization").assertIsDisplayed()
        composeRule.onNodeWithText("Done").assertIsDisplayed()
    }

    @Test
    fun batteryStepRequestsBatteryOptimizationExclusion() {
        val launched = mutableListOf<SystemSettingsIntentSpec>()
        setContent(onOpenSettings = { launched.add(it) })

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Exclude from battery optimization").performClick()

        assertEquals(1, launched.size)
        assertEquals("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", launched[0].action)
        assertEquals("com.remainder.app", launched[0].packageName)
    }

    @Test
    fun batteryStepLinksToBatterySettingsList() {
        val launched = mutableListOf<SystemSettingsIntentSpec>()
        setContent(onOpenSettings = { launched.add(it) })

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Open battery settings").performClick()

        assertEquals(1, launched.size)
        assertEquals("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS", launched[0].action)
    }

    @Test
    fun backReturnsToPreviousStep() {
        setContent()

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Back").performClick()

        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
    }

    @Test
    fun backIsNotOfferedOnFirstStep() {
        setContent()

        composeRule.onNodeWithText("Back").assertDoesNotExist()
    }

    @Test
    fun doneOnLastStepInvokesCompletionCallback() {
        var completed = false
        setContent(onCompleted = { completed = true })

        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Done").performClick()

        assertTrue(completed)
    }

    @Test
    fun softDenialShowsRetryGuidance() {
        var requested = false
        setContent(
            guidance = NotificationGuidance.RetryRequest,
            onRequestPermission = { requested = true }
        )

        composeRule.onNodeWithText("Try again").performClick()

        assertTrue(requested)
    }

    @Test
    fun permanentDenialHidesRuntimeRequestButton() {
        setContent(guidance = NotificationGuidance.OpenAppSettings)

        composeRule.onNodeWithText("Allow notifications").assertDoesNotExist()
        composeRule.onNodeWithText("Open app settings").assertIsDisplayed()
    }

    @Test
    fun permanentDenialShowsGuidanceAndOpensAppSettings() {
        val launched = mutableListOf<SystemSettingsIntentSpec>()
        setContent(
            guidance = NotificationGuidance.OpenAppSettings,
            onOpenSettings = { launched.add(it) }
        )

        composeRule
            .onNodeWithText("Notifications are blocked for Remainder. Open Android app settings and enable notifications to receive reminders.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Open app settings").performClick()

        assertEquals(1, launched.size)
        assertEquals("android.settings.APPLICATION_DETAILS_SETTINGS", launched[0].action)
        assertEquals("com.remainder.app", launched[0].packageName)
    }
}
