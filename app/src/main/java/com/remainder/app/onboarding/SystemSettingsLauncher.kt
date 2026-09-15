package com.remainder.app.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

interface SystemSettingsLauncher {
    fun launch(spec: SystemSettingsIntentSpec)
}

class ContextSystemSettingsLauncher(private val context: Context) : SystemSettingsLauncher {
    override fun launch(spec: SystemSettingsIntentSpec) {
        context.startActivity(toIntent(spec))
    }

    private fun toIntent(spec: SystemSettingsIntentSpec): Intent {
        val intent = Intent(spec.action)
        spec.packageName?.let { packageName ->
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            intent.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
            if (spec.usePackageUri) {
                intent.data = Uri.parse("package:$packageName")
            }
        }
        spec.channelId?.let { channelId ->
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
        }
        return intent
    }
}
