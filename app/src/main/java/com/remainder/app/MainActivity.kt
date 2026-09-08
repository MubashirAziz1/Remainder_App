package com.remainder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.remainder.app.notification.LockScreenNotificationCoordinator
import com.remainder.app.ui.RemainderApp
import com.remainder.app.ui.theme.RemainderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val coordinator = LockScreenNotificationCoordinator.create(this)
        coordinator.sync()
        setContent {
            RemainderTheme {
                RemainderApp(coordinator)
            }
        }
    }
}
