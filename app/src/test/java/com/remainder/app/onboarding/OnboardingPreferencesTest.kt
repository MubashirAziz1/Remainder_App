package com.remainder.app.onboarding

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
class OnboardingPreferencesTest {

    private lateinit var store: OnboardingPreferences

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("remainder_onboarding", Context.MODE_PRIVATE)
            .edit().clear().commit()
        store = OnboardingPreferences(context)
    }

    @Test
    fun onboardingIsNotCompletedByDefault() {
        assertFalse(store.isCompleted())
    }

    @Test
    fun completionPersistsAcrossInstances() {
        store.markCompleted()

        assertTrue(OnboardingPreferences(RuntimeEnvironment.getApplication()).isCompleted())
    }
}
