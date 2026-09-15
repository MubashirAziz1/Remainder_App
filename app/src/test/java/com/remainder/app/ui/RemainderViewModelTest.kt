package com.remainder.app.ui

import com.remainder.app.data.RemainderPreferenceStore
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.notification.LockScreenNotifier
import com.remainder.app.notification.NotificationPermissionChecker
import com.remainder.app.onboarding.NotificationGuidance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RemainderViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateReflectsPreferenceDefaultsAndPermission() = runTest {
        val viewModel = viewModel(FakePreferenceStore(), FakeNotifier(), granted = true)

        val state = viewModel.uiState.value
        assertFalse(state.lockScreenReminderEnabled)
        assertFalse(state.onboardingCompleted)
        assertTrue(state.permissionGranted)
        assertTrue(state.startOnboarding)
    }

    @Test
    fun startOnboardingIsTrueUntilOnboardingCompleted() = runTest {
        val viewModel = viewModel(FakePreferenceStore(initialOnboarding = false), FakeNotifier(), granted = true)

        assertTrue(viewModel.uiState.value.startOnboarding)

        viewModel.onOnboardingCompleted()

        assertFalse(viewModel.uiState.value.startOnboarding)
        assertTrue(viewModel.uiState.value.onboardingCompleted)
    }

    @Test
    fun enablingWithPermissionPersistsAndShowsNotification() = runTest {
        val preferences = FakePreferenceStore()
        val notifier = FakeNotifier()
        val viewModel = viewModel(preferences, notifier, granted = true)

        viewModel.onLockScreenReminderChange(true)

        assertTrue(preferences.lockScreenValue)
        assertTrue(viewModel.uiState.value.lockScreenReminderEnabled)
        assertTrue(notifier.showing)
    }

    @Test
    fun enablingWithoutPermissionRequestsPermissionAndDoesNotPersistOrShow() = runTest {
        val preferences = FakePreferenceStore()
        val notifier = FakeNotifier()
        val viewModel = viewModel(preferences, notifier, granted = false)
        val events = mutableListOf<RemainderEvent>()
        backgroundScope.launch(Dispatchers.Main) { viewModel.events.collect { events.add(it) } }

        viewModel.onLockScreenReminderChange(true)

        assertEquals(listOf(RemainderEvent.RequestNotificationPermission), events)
        assertFalse(preferences.lockScreenValue)
        assertFalse(notifier.showing)
        assertFalse(viewModel.uiState.value.lockScreenReminderEnabled)
    }

    @Test
    fun disablingHidesNotificationAndPersists() = runTest {
        val preferences = FakePreferenceStore(initialLockScreen = true)
        val notifier = FakeNotifier()
        val viewModel = viewModel(preferences, notifier, granted = true)

        viewModel.onLockScreenReminderChange(false)

        assertFalse(preferences.lockScreenValue)
        assertFalse(notifier.showing)
    }

    @Test
    fun permissionGrantedPersistsShowsAndClearsDenial() = runTest {
        val preferences = FakePreferenceStore()
        val notifier = FakeNotifier()
        val viewModel = viewModel(preferences, notifier, granted = true)

        viewModel.onPermissionResult(granted = true, showRationale = false)

        assertTrue(preferences.lockScreenValue)
        assertTrue(notifier.showing)
        assertFalse(viewModel.uiState.value.denialObserved)
        assertTrue(viewModel.uiState.value.permissionGranted)
    }

    @Test
    fun permissionDeniedPersistsFalseHidesAndRecordsDenial() = runTest {
        val preferences = FakePreferenceStore(initialLockScreen = true)
        val notifier = FakeNotifier()
        val viewModel = viewModel(preferences, notifier, granted = false)

        viewModel.onPermissionResult(granted = false, showRationale = false)

        assertFalse(preferences.lockScreenValue)
        assertFalse(notifier.showing)
        assertTrue(viewModel.uiState.value.denialObserved)
    }

    @Test
    fun refreshRationaleUpdatesStateFromProvider() = runTest {
        val viewModel = viewModel(FakePreferenceStore(), FakeNotifier(), granted = false)

        assertFalse(viewModel.uiState.value.showNotificationRationale)

        viewModel.refreshRationale(showRationale = true)

        assertTrue(viewModel.uiState.value.showNotificationRationale)
    }

    @Test
    fun guidanceIsNoneWhenPermissionGranted() = runTest {
        val viewModel = viewModel(FakePreferenceStore(), FakeNotifier(), granted = true)
        assertEquals(NotificationGuidance.None, viewModel.uiState.value.notificationGuidance)
    }

    @Test
    fun guidanceIsRetryRequestAfterSoftDenialWithRationale() = runTest {
        val viewModel = viewModel(FakePreferenceStore(), FakeNotifier(), granted = false)

        viewModel.onPermissionResult(granted = false, showRationale = true)

        assertEquals(NotificationGuidance.RetryRequest, viewModel.uiState.value.notificationGuidance)
    }

    @Test
    fun guidanceIsOpenAppSettingsAfterDenialWithoutRationale() = runTest {
        val viewModel = viewModel(FakePreferenceStore(), FakeNotifier(), granted = false)

        viewModel.onPermissionResult(granted = false, showRationale = false)

        assertEquals(NotificationGuidance.OpenAppSettings, viewModel.uiState.value.notificationGuidance)
    }

    @Test
    fun syncReconcilesNotificationWithPersistedPreferenceAndPermission() = runTest {
        val notifier = FakeNotifier()
        val viewModel = viewModel(FakePreferenceStore(initialLockScreen = true), notifier, granted = true)

        viewModel.sync()

        assertTrue(notifier.channelEnsured)
        assertTrue(notifier.showing)
    }

    @Test
    fun syncHidesWhenEnabledButPermissionMissing() = runTest {
        val notifier = FakeNotifier()
        val viewModel = viewModel(FakePreferenceStore(initialLockScreen = true), notifier, granted = false)

        viewModel.sync()

        assertFalse(notifier.showing)
    }

    private fun TestScope.viewModel(
        preferences: FakePreferenceStore,
        notifier: FakeNotifier,
        granted: Boolean
    ): RemainderViewModel = RemainderViewModel(
        preferences = preferences,
        coordinator = LockScreenNotificationCoordinator(
            permission = NotificationPermissionChecker { granted },
            notifications = notifier
        )
    )
}

private class FakePreferenceStore(
    initialLockScreen: Boolean = false,
    initialOnboarding: Boolean = false
) : RemainderPreferenceStore {
    private val _lockScreen = MutableStateFlow(initialLockScreen)
    private val _onboarding = MutableStateFlow(initialOnboarding)
    override val lockScreenReminderEnabled: Flow<Boolean> = _lockScreen
    override val onboardingCompleted: Flow<Boolean> = _onboarding
    var lockScreenValue: Boolean
        get() = _lockScreen.value
        set(value) { _lockScreen.value = value }
    var onboardingValue: Boolean
        get() = _onboarding.value
        set(value) { _onboarding.value = value }

    override suspend fun setLockScreenReminderEnabled(enabled: Boolean) {
        _lockScreen.value = enabled
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        _onboarding.value = completed
    }
}

private class FakeNotifier : LockScreenNotifier {
    var showing: Boolean = false
    var channelEnsured: Boolean = false
    override fun show() { showing = true }
    override fun hide() { showing = false }
    override fun ensureChannel() { channelEnsured = true }
}
