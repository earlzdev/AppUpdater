package com.earldev.self_update

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.api.UpdaterApiRegistry
import com.earldev.app_update.api.models.CancelledInstallationException
import com.earldev.app_update.api.models.NoInstallPermissionException
import com.earldev.app_update.api.models.UnauthorizedException
import com.earldev.app_update.init.AppUpdateInitializer
import com.earldev.app_update.init.AppUpdaterInitConfig
import com.earldev.appupdater.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _screenStateFlow: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState())
    val screenStateFlow: StateFlow<ScreenState> = _screenStateFlow.asStateFlow()

    private val _updateCancelledTrigger: MutableSharedFlow<Unit> = MutableSharedFlow()
    val updateCancelledTrigger: SharedFlow<Unit> = _updateCancelledTrigger.asSharedFlow()

    val updateStepFlow: StateFlow<UpdateStep?> = UpdateStateHolder.currentStateFlow()
        .onEach(::handleUpdateStep)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun initAppUpdater(activity: ComponentActivity) {
        AppUpdateInitializer.initialize(
            activity = activity,
            config = AppUpdaterInitConfig(
                actualVersionCode = BuildConfig.VERSION_CODE,
                actualVersionName = BuildConfig.VERSION_NAME,
                downloadApkUrl = "http://10.0.2.2:8000/download",
                updateAvailabilityCheckUrl = "http://10.0.2.2:8000/check_version",
                bearerToken = "old_token",
                logEnabled = BuildConfig.DEBUG
            )
        )
    }

    fun onCheckUpdateClick() {
        viewModelScope.launch {
            _screenStateFlow.update { it.copy(loading = true) }
            UpdaterApiRegistry.updateAvailabilityUseCase.updateAvailable().onSuccess { updateAvailable ->
                _screenStateFlow.update {
                    it.copy(
                        updateAvailable = updateAvailable,
                        loading = false
                    )
                }
            }.onFailure {
                // ignore
                _screenStateFlow.update { it.copy(loading = false) }
            }
        }
    }

    fun onStartUpdateClick() {
        _screenStateFlow.update { it.copy(loading = true) }
        UpdaterApiRegistry.updateManager.startUpdate()
    }

    fun onStopUpdateClick() {
        UpdaterApiRegistry.updateManager.stopUpdate()
    }

    fun onAgreedToGivePermission() {
        _screenStateFlow.update { it.copy(noPermissionAlert = false, loading = true) }
        UpdaterApiRegistry.updateManager.onAgreedToGivePermission()
    }

    fun onDeclinedToGivePermission() {
        _screenStateFlow.update { it.copy(loading = false, noPermissionAlert = false) }
        UpdaterApiRegistry.updateManager.onDeclinedToGivePermission()
    }

    private suspend fun handleUpdateStep(updateStep: UpdateStep?) {
        updateStep?.failure?.let {
            _screenStateFlow.update { it.copy(loading = false) }
        }
        when (updateStep?.failure) {
            is NoInstallPermissionException -> {
                _screenStateFlow.update { it.copy(noPermissionAlert = true) }
            }
            is CancelledInstallationException -> {
                _updateCancelledTrigger.emit(Unit)
            }
            is UnauthorizedException -> {
                _screenStateFlow.update { it.copy(loading = true) }
                // TODO: Refresh token
                delay(3000L)

                UpdaterApiRegistry.updateManager.updateTokenAndRetry("new_token")
            }
        }
    }
}