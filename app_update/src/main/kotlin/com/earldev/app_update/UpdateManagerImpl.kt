package com.earldev.app_update

import android.content.Context
import com.earldev.app_update.api.UpdateManager
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.models.DeclinedToGivePermissionException
import com.earldev.app_update.apk.ApkDownloader
import com.earldev.app_update.apk.ApkInstaller
import com.earldev.app_update.apk.ApkSecurityChecker
import com.earldev.app_update.apk.AvailableApkChecker
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.permission.InstallPermissionLauncher
import com.earldev.app_update.usecase.SaveAndDeleteApkUseCase
import com.earldev.app_update.utils.SelfUpdateLog
import com.earldev.app_update.utils.UpdateCoroutineScopeHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class UpdateManagerImpl @Inject constructor(
    private val context: Context,
    private val updateAvailabilityUseCase: UpdateAvailabilityUseCase,
    private val dataStore: PreferencesDataStore,
    private val installPermissionLauncher: InstallPermissionLauncher,
    private val saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase,
    private val apkFileNameProvider: ApkFileNameProvider,
    private val coroutineScopeHolder: UpdateCoroutineScopeHolder
) : UpdateManager {

    private var updateJob: Job? = null
    private var updateProcessChain: AvailableApkChecker? = null

    override fun startUpdate() {
        SelfUpdateLog.logInfo("Start updating")

        if (SelfUpdateStore.updateStarted() == true) {
            SelfUpdateLog.logInfo("Update has already started!")
            return
        }

        stopPreviousUpdateProcess()
        initUpdateChain()

        updateJob = coroutineScopeHolder.coroutineScope().launch {
            try {
                updateProcessChain?.start()
            } catch (e: Exception) {
                UpdateStateHolder.emit(
                    UpdateStep.Finish(
                        success = false,
                        failure = e
                    )
                )
            } finally {
                stopPreviousUpdateProcess()
            }
        }
        SelfUpdateStore.setUpdateStarted(true)
    }

    override fun stopUpdate() {
        SelfUpdateLog.logInfo("Update stop")
        stopPreviousUpdateProcess()
    }

    override fun onAgreedToGivePermission() {
        SelfUpdateLog.logInfo("Agreed to give permission")
        installPermissionLauncher.requestPermission(context)
    }

    override fun onDeclinedToGivePermission() {
        SelfUpdateLog.logInfo("Declined to give permission")
        UpdateStateHolder.emit(
            UpdateStep.Finish(
                success = false,
                failure = DeclinedToGivePermissionException()
            )
        )
        stopPreviousUpdateProcess()
    }

    private fun stopPreviousUpdateProcess() {
        SelfUpdateStore.clear()
        saveAndDeleteApkUseCase.remove()
        updateJob?.cancel()
        coroutineScopeHolder.clear()
        updateProcessChain = null
    }

    private fun initUpdateChain() {
        updateProcessChain = AvailableApkChecker(
            nextHandler = ApkDownloader(
                nextHandler = ApkSecurityChecker(
                    nextHandler = ApkInstaller(
                        context = context,
                        dataStore = dataStore,
                        installPermissionLauncher = installPermissionLauncher,
                        apkFileNameProvider = apkFileNameProvider
                    ),
                    context = context,
                    apkFileNameProvider = apkFileNameProvider
                ),
                context = context,
            ),
            updateAvailabilityUseCase = updateAvailabilityUseCase,
        )
    }
}