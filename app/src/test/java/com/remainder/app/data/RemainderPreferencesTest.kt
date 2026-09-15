package com.remainder.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
class RemainderPreferencesTest {

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("remainder_notifications", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("remainder_onboarding", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun lockScreenReminderIsDisabledByDefault() = runTest {
        assertFalse(preferences().lockScreenReminderEnabled.first())
    }

    @Test
    fun onboardingIsNotCompletedByDefault() = runTest {
        assertFalse(preferences().onboardingCompleted.first())
    }

    @Test
    fun enablingLockScreenReminderPersistsAcrossInstances() = runTest {
        val preferences = preferences()
        preferences.setLockScreenReminderEnabled(true)

        assertTrue(preferences().lockScreenReminderEnabled.first())
    }

    @Test
    fun disablingLockScreenReminderPersists() = runTest {
        val preferences = preferences()
        preferences.setLockScreenReminderEnabled(true)
        preferences.setLockScreenReminderEnabled(false)

        assertFalse(preferences().lockScreenReminderEnabled.first())
    }

    @Test
    fun markingOnboardingCompletedPersistsAcrossInstances() = runTest {
        val preferences = preferences()
        preferences.setOnboardingCompleted(true)

        assertTrue(preferences().onboardingCompleted.first())
    }

    @Test
    fun preferenceKeysAreIndependent() = runTest {
        val preferences = preferences()
        preferences.setOnboardingCompleted(true)

        assertFalse(preferences.lockScreenReminderEnabled.first())
    }

    @Test
    fun migrationsTargetBothLegacyPreferenceFiles() {
        val migrations = remainderMigrations(RuntimeEnvironment.getApplication())

        assertEquals(2, migrations.size)
    }

    @Test
    fun lockScreenMigrationRunsWhenLegacyValuePresent() = runTest {
        val context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("remainder_notifications", Context.MODE_PRIVATE)
            .edit().putBoolean("lock_screen_reminder_enabled", true).commit()

        val migration = remainderMigrations(context).first()
        assertTrue(migration.shouldMigrate(emptyPreferences()))
        val migrated = migration.migrate(emptyPreferences())

        assertTrue(migrated[booleanPreferencesKey("lock_screen_reminder_enabled")] ?: false)
    }

    @Test
    fun lockScreenMigrationDoesNotRunWhenLegacyAbsent() = runTest {
        val migration = remainderMigrations(RuntimeEnvironment.getApplication()).first()

        assertFalse(migration.shouldMigrate(emptyPreferences()))
    }

    @Test
    fun onboardingMigrationRunsWhenLegacyValuePresent() = runTest {
        val context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("remainder_onboarding", Context.MODE_PRIVATE)
            .edit().putBoolean("onboarding_completed", true).commit()

        val migration = remainderMigrations(context)[1]
        assertTrue(migration.shouldMigrate(emptyPreferences()))
        val migrated = migration.migrate(emptyPreferences())

        assertTrue(migrated[booleanPreferencesKey("onboarding_completed")] ?: false)
    }

    private fun preferences(): RemainderPreferences =
        RemainderPreferences(RuntimeEnvironment.getApplication())
}
