package com.remainder.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.data.RemainderPreferenceStore
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.onboarding.DeniedPermissionGuidance
import com.remainder.app.onboarding.NotificationGuidance
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Composer/UI state for the app shell. Only the two durable flags
 * ([lockScreenReminderEnabled], [onboardingCompleted]) are persisted (via DataStore);
 * the remaining fields are transient and live only in the ViewModel.
 */
data class RemainderUiState(
    val lockScreenReminderEnabled: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val permissionGranted: Boolean = false,
    val denialObserved: Boolean = false,
    val showNotificationRationale: Boolean = false
) {
    val startOnboarding: Boolean get() = !onboardingCompleted

    val notificationGuidance: NotificationGuidance
        get() = DeniedPermissionGuidance.resolve(
            permissionGranted = permissionGranted,
            denialObserved = denialObserved,
            showRationale = showNotificationRationale
        )
}

/** One-shot effects the ViewModel asks the View (Activity) to perform. */
sealed interface RemainderEvent {
    data object RequestNotificationPermission : RemainderEvent
}

// VIEWMODEL

/**
 * Owns composer/UI state for the app shell and orchestrates lock-screen notification
 * side-effects. Durable preferences are read from [RemainderPreferenceStore] (DataStore);
 * everything else is in-memory state that is not persisted.
 */
class RemainderViewModel(
    private val preferences: RemainderPreferenceStore,
    private val coordinator: LockScreenNotificationCoordinator
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemainderUiState())
    val uiState: StateFlow<RemainderUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RemainderEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<RemainderEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            preferences.lockScreenReminderEnabled.collect { enabled ->
                _uiState.update { it.copy(lockScreenReminderEnabled = enabled) }
                reconcileNotification(enabled)
            }
        }
        viewModelScope.launch {
            preferences.onboardingCompleted.collect { completed ->
                _uiState.update { it.copy(onboardingCompleted = completed) }
            }
        }
    }

    /**
     * Re-reads the runtime permission and reconciles the notification with the
     * persisted preference. Called on Activity resume and after permission changes.
     */
    fun sync() {
        reconcileNotification(_uiState.value.lockScreenReminderEnabled)
    }

    /**
     * Stores the latest notification-permission rationale flag, read by the View
     * (Activity) on resume and after a permission result. Keeping this a value pushed
     * from the View avoids the retained ViewModel capturing an Activity.
     */
    fun refreshRationale(showRationale: Boolean) {
        _uiState.update { it.copy(showNotificationRationale = showRationale) }
    }

    fun onLockScreenReminderChange(wantEnabled: Boolean) {
        if (!wantEnabled) {
            viewModelScope.launch { preferences.setLockScreenReminderEnabled(false) }
            return
        }
        if (!coordinator.hasPermission()) {
            _events.tryEmit(RemainderEvent.RequestNotificationPermission)
            return
        }
        viewModelScope.launch { preferences.setLockScreenReminderEnabled(true) }
    }

    fun onPermissionResult(granted: Boolean, showRationale: Boolean) {
        // Re-read the rationale so post-dialog guidance reflects the current state
        // (e.g. rationale becomes available after a soft denial).
        refreshRationale(showRationale)
        if (granted) {
            _uiState.update { it.copy(denialObserved = false, permissionGranted = true) }
            viewModelScope.launch { preferences.setLockScreenReminderEnabled(true) }
        } else {
            _uiState.update { it.copy(denialObserved = true, permissionGranted = false) }
            viewModelScope.launch { preferences.setLockScreenReminderEnabled(false) }
        }
    }

    fun onOnboardingCompleted() {
        viewModelScope.launch { preferences.setOnboardingCompleted(true) }
    }

    private fun reconcileNotification(desiredEnabled: Boolean) {
        val granted = coordinator.hasPermission()
        _uiState.update { it.copy(permissionGranted = granted) }
        coordinator.sync(desiredEnabled)
    }
}
