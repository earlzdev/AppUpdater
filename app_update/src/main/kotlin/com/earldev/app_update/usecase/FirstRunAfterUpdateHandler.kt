package com.earldev.app_update.usecase

import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.datastore.PreferencesDataStore
import javax.inject.Inject

internal class FirstRunAfterUpdateHandler @Inject constructor(
    private val dataStore: PreferencesDataStore,
    private val saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase
) {

    fun handleFirstStartIfNeeded() {
        val updateHasStarted = dataStore.updateStartedFlag()
        if (updateHasStarted) {
            UpdateStateHolder.emit(UpdateStep.Finish(success = true))
            dataStore.saveUpdateStartedFlag(false)
            saveAndDeleteApkUseCase.remove()
        }
    }
}