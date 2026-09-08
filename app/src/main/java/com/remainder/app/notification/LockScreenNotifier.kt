package com.remainder.app.notification

interface LockScreenNotifier {
    fun show()
    fun hide()
    fun ensureChannel()
}
