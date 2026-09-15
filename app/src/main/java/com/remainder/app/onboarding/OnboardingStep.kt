package com.remainder.app.onboarding

enum class OnboardingStep(val title: String, val body: String) {
    Notifications(
        title = "Notifications",
        body = "Remainder posts a persistent reminder. Allow notifications so you never miss it."
    ),
    LockScreen(
        title = "Lock screen",
        body = "The reminder is public on your lock screen. You can adjust lock-screen visibility in the channel settings."
    ),
    BatteryOptimization(
        title = "Battery optimization",
        body = "Exclude Remainder from battery optimization so reminders are not delayed."
    );

    val isLast: Boolean get() = ordinal == entries.size - 1

    fun next(): OnboardingStep? = entries.getOrNull(ordinal + 1)

    fun previous(): OnboardingStep? = entries.getOrNull(ordinal - 1)

    companion object {
        val first: OnboardingStep get() = entries.first()
        val ordered: List<OnboardingStep> get() = entries.toList()
    }
}
