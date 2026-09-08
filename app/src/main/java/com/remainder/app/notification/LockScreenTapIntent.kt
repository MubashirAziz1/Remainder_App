package com.remainder.app.notification

import android.content.Context
import android.content.Intent
import com.remainder.app.MainActivity

object LockScreenTapIntent {
    fun create(context: Context): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}
