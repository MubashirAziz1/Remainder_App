package com.remainder.app.alarm

import com.remainder.app.testutil.ProjectPaths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the keyguard/lock-screen launch path against regressions that would
 * break behavior on specific Android versions or OEM ROMs.
 *
 * The modern, OEM-respected way to show an Activity over the keyguard and turn
 * the screen on is the API 27+ Activity methods `setShowWhenLocked` /
 * `setTurnScreenOn` plus the matching manifest attributes. The pre-27 window
 * flags (`FLAG_SHOW_WHEN_LOCKED`, `FLAG_TURN_SCREEN_ON`, `FLAG_DISMISS_KEYGUARD`)
 * are deprecated and behave inconsistently across OEMs (notably older MIUI and
 * Huawei builds), so the source must not regress to them.
 *
 * Assertions run against executable source only — comments and string
 * literals are stripped first so a doc reference cannot produce a false
 * positive/negative.
 */
class LockScreenAlarmKeyguardApiTest {

    private val activitySource: String
        get() = stripNonExecutable(
            ProjectPaths.appDir
                .resolve("src/main/java/com/remainder/app/LockScreenAlarmActivity.kt")
                .readText()
        )

    @Test
    fun usesModernShowWhenLockedActivityApi() {
        assertTrue(
            "LockScreenAlarmActivity must call setShowWhenLocked(true) (API 27+ Activity API)",
            activitySource.contains("setShowWhenLocked(true)")
        )
    }

    @Test
    fun usesModernTurnScreenOnActivityApi() {
        assertTrue(
            "LockScreenAlarmActivity must call setTurnScreenOn(true) (API 27+ Activity API)",
            activitySource.contains("setTurnScreenOn(true)")
        )
    }

    @Test
    fun doesNotUseDeprecatedKeyguardWindowFlags() {
        assertFalse(
            "Avoid deprecated FLAG_SHOW_WHEN_LOCKED; use the API 27+ Activity method",
            activitySource.contains("FLAG_SHOW_WHEN_LOCKED")
        )
        assertFalse(
            "Avoid deprecated FLAG_TURN_SCREEN_ON; use the API 27+ Activity method",
            activitySource.contains("FLAG_TURN_SCREEN_ON")
        )
        assertFalse(
            "Avoid deprecated FLAG_DISMISS_KEYGUARD; OEMs handle keyguard dismissal inconsistently",
            activitySource.contains("FLAG_DISMISS_KEYGUARD")
        )
    }

    /**
     * Removes block comments, line comments, and double-quoted string literals
     * so assertions inspect executable code only. Good enough for a focused
     * source guard on a small, known file.
     */
    private fun stripNonExecutable(source: String): String {
        var s = source
        s = Regex("""/\*[\s\S]*?\*/""").replace(s, "")   // block comments
        s = Regex("""//[^\n]*""").replace(s, "")          // line comments
        s = Regex("""\"(?:[^\"\\]|\\.)*\"""").replace(s, "\"\"") // string literals -> ""
        return s
    }
}
