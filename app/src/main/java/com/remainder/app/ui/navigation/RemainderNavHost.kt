package com.remainder.app.ui.navigation

import androidx.compose.runtime.Composable
import com.remainder.app.ui.home.HomeScreen

@Composable
fun RemainderNavHost() {
    HomeScreen(onOpenSettings = {})
}
