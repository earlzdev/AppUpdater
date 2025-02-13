package com.earldev.app_update.usecase

import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.datastore.PreferencesDataStore
import javax.inject.Inject

/**
 * Handles the first launch after an app update.
 *
 * This class is responsible for performing necessary actions when the app is launched for the first time
 * after an update. Specifically, it checks whether an update process was started, and if so, it finalizes
 * the update, updates the corresponding flag in the data store, and deletes the saved APK file if present.
 *
 * @property dataStore The data store used to manage update-related flags.
 * @property saveAndDeleteApkUseCase Use case for saving and deleting the APK file.
 */
internal class FirstRunAfterUpdateHandler @Inject constructor(
    private val dataStore: PreferencesDataStore,
    private val saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase
) {

    /**
     * Performs necessary actions on the first launch after an update, if required.
     *
     * This method checks whether an update process was started. If an update was initiated:
     * 1. It emits an event indicating the update has been completed.
     * 2. It resets the update flag in the data store.
     * 3. It removes the saved APK file, if it exists.
     */
    fun handleFirstStartIfNeeded() {
        val updateHasStarted = dataStore.updateStartedFlag()
        if (updateHasStarted) {
            UpdateStateHolder.emit(UpdateStep.Finish(success = true))
            dataStore.saveUpdateStartedFlag(false)
            saveAndDeleteApkUseCase.remove()
        }
    }
}