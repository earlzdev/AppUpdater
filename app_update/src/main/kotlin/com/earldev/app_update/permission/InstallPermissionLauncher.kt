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

/**
 * Manages the request for permission to install applications from unknown sources.
 *
 * Uses [ActivityResultLauncher] to request permission and [MutableSharedFlow] to
 * track the permission status.
 */
internal class InstallPermissionLauncher @Inject constructor() {

    private var launcher: ActivityResultLauncher<Intent>? = null

    private val permissionStatusFlow: MutableSharedFlow<InstallPermissionStatus> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * Initializes the [ActivityResultLauncher] for handling the permission request result.
     *
     * @param activity The [ComponentActivity] where the permission request will be made.
     */
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

    /**
     * Requests permission to install an APK.
     *
     * @param context The [Context] used to check the permission.
     */
    fun requestPermission(context: Context) {
        if (installPermissionGranted(context)) {
            permissionStatusFlow.tryEmit(InstallPermissionStatus.Granted)
        } else {
            requestInstallPermission(context.packageName)
        }
    }

    /**
     * Checks if the permission to install APKs is granted.
     *
     * @param context The [Context] used for checking the permission.
     * @return `true` if the permission is granted, otherwise `false`.
     */
    fun installPermissionGranted(context: Context): Boolean =
        context.packageManager.canRequestPackageInstalls()

    /**
     * Returns a [SharedFlow] to observe the permission status.
     *
     * @return A [SharedFlow] of [InstallPermissionStatus] representing the current permission state.
     */
    fun permissionStatusFlow(): SharedFlow<InstallPermissionStatus> = permissionStatusFlow

    /**
     * Opens the system settings screen to request install permission.
     *
     * @param packageName The package name of the application requesting the permission.
     */
    private fun requestInstallPermission(packageName: String) {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:$packageName")
        launcher?.launch(intent)
    }
}