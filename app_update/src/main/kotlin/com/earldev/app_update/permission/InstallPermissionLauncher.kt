package com.earldev.app_update.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

internal class InstallPermissionLauncher @Inject constructor() {

    private var launcher: ActivityResultLauncher<Intent>? = null

    private val permissionStatusFlow: MutableSharedFlow<InstallPermissionStatus> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun init(activity: ComponentActivity) {
        launcher = null
        if (!installPermissionGranted(activity)) {
            launcher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (installPermissionGranted(activity)) {
                    permissionStatusFlow.tryEmit(InstallPermissionStatus.Granted)
                } else {
                    permissionStatusFlow.tryEmit(InstallPermissionStatus.Denied)
                }
            }
        } else {
            permissionStatusFlow.tryEmit(InstallPermissionStatus.Granted)
        }
    }

    fun requestPermission(context: Context) {
        if (installPermissionGranted(context)) {
            permissionStatusFlow.tryEmit(InstallPermissionStatus.Granted)
        } else {
            requestInstallPermission(context.packageName)
        }
    }

    fun installPermissionGranted(context: Context): Boolean =
        context.packageManager.canRequestPackageInstalls()

    fun permissionStatusFlow(): SharedFlow<InstallPermissionStatus> = permissionStatusFlow

    private fun requestInstallPermission(packageName: String) {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:$packageName")
        launcher?.launch(intent)
    }
}