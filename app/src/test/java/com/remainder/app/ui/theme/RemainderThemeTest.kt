package com.remainder.app.ui.theme

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RemainderThemeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun appliesLightScheme() {
        composeRule.setContent {
            RemainderTheme(darkTheme = false) {}
        }
    }

    @Test
    fun appliesDarkScheme() {
        composeRule.setContent {
            RemainderTheme(darkTheme = true) {}
        }
    }
}
