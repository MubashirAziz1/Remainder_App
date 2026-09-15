package com.remainder.app.onboarding

import android.content.Context

interface OnboardingStore {
    fun isCompleted(): Boolean
    fun markCompleted()
}

class OnboardingPreferences(context: Context) : OnboardingStore {
    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun isCompleted(): Boolean = prefs.getBoolean(KEY, false)

    override fun markCompleted() {
        prefs.edit().putBoolean(KEY, true).commit()
    }

    private companion object {
        const val PREFS_NAME = "remainder_onboarding"
        const val KEY = "onboarding_completed"
    }
}
