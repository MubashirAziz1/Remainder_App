package com.remainder.app.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Contract for persisting the essential Remainder preferences. Implemented by the
 * DataStore-backed [RemainderPreferences]; faked in [com.remainder.app.ui.RemainderViewModelTest].
 */
interface RemainderPreferenceStore {
    val lockScreenReminderEnabled: Flow<Boolean>
    val onboardingCompleted: Flow<Boolean>
    suspend fun setLockScreenReminderEnabled(enabled: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
}

/**
 * Persists only the essential user preferences for Remainder using Jetpack DataStore.
 *
 * Transient UI/composer state is intentionally NOT persisted here; it lives in
 * [com.remainder.app.ui.RemainderViewModel]. Only two durable flags are stored:
 * whether the lock-screen reminder notification should be visible, and whether
 * onboarding has been completed.
 *
 * On first access the values previously held in the legacy `remainder_notifications`
 * and `remainder_onboarding` SharedPreferences are migrated so upgrading users keep
 * their settings. Read/write [IOException]s are treated as recoverable: reads fall back
 * to defaults and writes are logged rather than crashing the app.
 */
class RemainderPreferences(private val context: Context) : RemainderPreferenceStore {

    override val lockScreenReminderEnabled: Flow<Boolean> = preferenceFlow(KEY_LOCK_SCREEN_REMINDER)

    override val onboardingCompleted: Flow<Boolean> = preferenceFlow(KEY_ONBOARDING_COMPLETED)

    override suspend fun setLockScreenReminderEnabled(enabled: Boolean) {
        write(KEY_LOCK_SCREEN_REMINDER, enabled)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        write(KEY_ONBOARDING_COMPLETED, completed)
    }

    private fun preferenceFlow(key: androidx.datastore.preferences.core.Preferences.Key<Boolean>): Flow<Boolean> =
        context.remainderDataStore.data
            .catch { error ->
                if (error is IOException) {
                    Log.w(TAG, "Failed to read preferences; using defaults", error)
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences -> preferences[key] ?: false }

    private suspend fun write(key: androidx.datastore.preferences.core.Preferences.Key<Boolean>, value: Boolean) {
        try {
            context.remainderDataStore.edit { preferences -> preferences[key] = value }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to persist ${key.name}=$value", e)
        }
    }

    private companion object {
        private const val TAG = "RemainderPreferences"
        val KEY_LOCK_SCREEN_REMINDER = booleanPreferencesKey("lock_screen_reminder_enabled")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}

private val Context.remainderDataStore by preferencesDataStore(
    name = "remainder_preferences",
    produceMigrations = { context -> remainderMigrations(context) }
)

private const val LEGACY_NOTIFICATIONS = "remainder_notifications"
private const val LEGACY_ONBOARDING = "remainder_onboarding"

/** Migrates the two essential flags from their legacy SharedPreferences files into DataStore. */
internal fun remainderMigrations(
    context: Context
): List<androidx.datastore.core.DataMigration<androidx.datastore.preferences.core.Preferences>> = listOf(
    SharedPreferencesMigration(
        context = context,
        sharedPreferencesName = LEGACY_NOTIFICATIONS,
        keysToMigrate = setOf("lock_screen_reminder_enabled")
    ),
    SharedPreferencesMigration(
        context = context,
        sharedPreferencesName = LEGACY_ONBOARDING,
        keysToMigrate = setOf("onboarding_completed")
    )
)
