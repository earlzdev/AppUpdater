package com.earldev.app_update.apk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import com.earldev.app_update.ApkFileNameProvider
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.models.CancelledInstallationException
import com.earldev.app_update.api.models.NoInstallPermissionException
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.models.UpdateJob
import com.earldev.app_update.permission.InstallPermissionLauncher
import com.earldev.app_update.permission.InstallPermissionStatus
import com.earldev.app_update.utils.SelfUpdateLog
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume

internal class ApkInstaller @Inject constructor(
    private val context: Context,
    private val dataStore: PreferencesDataStore,
    private val installPermissionLauncher: InstallPermissionLauncher,
    private val apkFileNameProvider: ApkFileNameProvider,
) : ApkHandler() {

    override fun canHandle(job: UpdateJob): Boolean = job.secured

    override suspend fun handle(job: UpdateJob) {
        SelfUpdateLog.logInfo("Apk install start")

        if (installPermissionLauncher.installPermissionGranted(context)) {
            permissionGrantedAction()
            return
        } else {
            permissionDeniedAction()
        }

        installPermissionLauncher.permissionStatusFlow().collect { permissionStatus ->
            when (permissionStatus) {
                InstallPermissionStatus.Granted -> permissionGrantedAction()
                InstallPermissionStatus.Denied -> permissionDeniedAction()
            }
        }
    }

    private suspend fun permissionGrantedAction() {
        SelfUpdateLog.logInfo("Granted Permission")
        UpdateStateHolder.emit(UpdateStep.InstallApk(started = true))
        installApk()
        waitForInstalling()
        throw CancelledInstallationException()
    }

    private fun permissionDeniedAction() {
        SelfUpdateLog.logInfo("No install permission")
        UpdateStateHolder.emit(
            UpdateStep.InstallApk(
                success = false,
                failure = NoInstallPermissionException()
            )
        )
    }

    private fun installApk() {
        SelfUpdateLog.logInfo("Start installing apk")
        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            apkFileNameProvider.provide()
        )

        if (!apkFile.exists()) {
            SelfUpdateLog.logInfo("APK file does not exist.")
            throw IllegalArgumentException("APK file not found")
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".provider", apkFile)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        dataStore.saveUpdateStartedFlag(true)
        context.startActivity(intent)
    }

    private suspend fun waitForInstalling(): Unit = suspendCancellableCoroutine { continuation ->
        val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable {
            continuation.resume(Unit)
        }

        handler.postDelayed(runnable, 7_000)

        continuation.invokeOnCancellation {
            handler.removeCallbacks(runnable)
        }
    }
}