package com.earldev.app_update

import android.content.Context
import android.util.Log
import com.earldev.app_update.UpdateStep.Companion.DOWNLOAD_APK_STEP_KEY
import com.earldev.app_update.UpdateStep.Companion.INSTALL_APK_STEP_KEY
import com.earldev.app_update.UpdateStep.Companion.SECURITY_CHECK_STEP_KEY
import com.earldev.app_update.UpdateStep.Companion.UPDATE_AVAILABLE_STEP_KEY
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.api.UpdateManager
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.models.CancelledInstallationException
import com.earldev.app_update.api.models.DeclinedToGivePermissionException
import com.earldev.app_update.api.models.UnauthorizedException
import com.earldev.app_update.apk.ApkDownloader
import com.earldev.app_update.apk.ApkHandler
import com.earldev.app_update.apk.ApkInstaller
import com.earldev.app_update.apk.ApkSecurityChecker
import com.earldev.app_update.apk.AvailableApkChecker
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.permission.InstallPermissionLauncher
import com.earldev.app_update.usecase.SaveAndDeleteApkUseCase
import com.earldev.app_update.utils.SelfUpdateLog
import com.earldev.app_update.utils.UpdateCoroutineScopeHolder
import kotlinx.coroutines.CancellationException
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
    private val updateStepsHm = HashMap<Int, ApkHandler>()

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
                SelfUpdateStore.setUpdateStarted(true)
            } catch(e: CancellationException) {
                throw e
            } catch(e: Exception) {
                UpdateStateHolder.emit(
                    UpdateStep.Finish(
                        success = false,
                        failure = e
                    )
                )
            } finally {
                SelfUpdateLog.logInfo("Update ends")
                if (UpdateStateHolder.currentState()?.failure !is UnauthorizedException) {
                    stopPreviousUpdateProcess()
                }
            }
        }
    }

    override fun stopUpdate() {
        SelfUpdateLog.logInfo("Update stop")
        UpdateStateHolder.emit(UpdateStep.Finish(success = false, failure = CancelledInstallationException()))
        stopPreviousUpdateProcess()
        updateStepsHm.clear()
    }

    override fun updateTokenAndRetry(token: String) {
        SelfUpdateLog.logInfo("Update token and retry")
        SelfUpdateStore.setToken(token)

        updateJob?.cancel()
        updateJob = coroutineScopeHolder.coroutineScope().launch {
            try {
                val currentStep: ApkHandler = requireNotNull(updateStepsHm[UpdateStateHolder.currentStateFlow().value?.key]) {
                    "Current handler not found"
                }
                currentStep.retry()
            } catch (e: CancellationException) {
                throw e
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
        SelfUpdateLog.logInfo("Stop previous update process")
        SelfUpdateStore.clear()
        saveAndDeleteApkUseCase.remove()
        updateJob?.cancel()
        coroutineScopeHolder.clear()
        updateProcessChain = null
        SelfUpdateStore.setUpdateStarted(false)
    }

    private fun initUpdateChain() {
        updateStepsHm.clear()
        val installer = ApkInstaller(
            context = context,
            dataStore = dataStore,
            installPermissionLauncher = installPermissionLauncher,
            apkFileNameProvider = apkFileNameProvider
        )
        val securityChecker = ApkSecurityChecker(installer, context, apkFileNameProvider)
        val apkDownloader = ApkDownloader(securityChecker, context)
        val updateAvailableChecker = AvailableApkChecker(apkDownloader, updateAvailabilityUseCase)

        updateStepsHm[UPDATE_AVAILABLE_STEP_KEY] = updateAvailableChecker
        updateStepsHm[DOWNLOAD_APK_STEP_KEY] = apkDownloader
        updateStepsHm[SECURITY_CHECK_STEP_KEY] = securityChecker
        updateStepsHm[INSTALL_APK_STEP_KEY] = installer

        updateProcessChain = updateAvailableChecker
    }
}