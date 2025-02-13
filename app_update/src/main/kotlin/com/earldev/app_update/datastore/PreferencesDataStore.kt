package com.earldev.app_update.datastore

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Class for working with the local storage, using SharedPreferences
 * to save and retrieve data related to the app updates.
 *
 * @param context [Context] of the application
 */
internal class PreferencesDataStore @Inject constructor(
    context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(UPDATER_PREFS, Context.MODE_PRIVATE)

    /**
     * Saves the URL for downloading the APK.
     *
     * @param url URL of the APK file to be downloaded
     */
    fun saveApkDownloadUrl(url: String) = save {
        putString(DOWNLOAD_URL, url)
    }

    /**
     * Saves the URL for checking update availability.
     *
     * @param url URL for checking updates
     */
    fun saveUpdateAvailabilityCheckUrl(url: String) = save {
        putString(UPDATE_AVAILABILITY_URL, url)
    }

    /**
     * Saves the current app version code.
     *
     * @param versionCode The app's version in numeric format
     */
    fun saveActualVersionCode(versionCode: Int) = save {
        putInt(VERSION_CODE, versionCode)
    }

    /**
     * Saves the current app version name.
     *
     * @param versionName The app's version in string format
     */
    fun saveActualVersionName(versionName: String) = save {
        putString(VERSION_NAME, versionName)
    }

    /**
     * Saves the flag indicating whether the update has started.
     *
     * @param updateStarted Flag indicating if the update has started
     */
    fun saveUpdateStartedFlag(updateStarted: Boolean) = save {
        putBoolean(UPDATED_STARTED, updateStarted)
    }

    /**
     * Retrieves the current app version name.
     *
     * @return The current version name of the app or null
     */
    fun getActualVersionName(): String? = sharedPreferences.getString(VERSION_NAME, null)

    /**
     * Retrieves the current app version code.
     *
     * @return The current version code of the app or -1 if not set
     */
    fun getActualVersionCode(): Int = sharedPreferences.getInt(VERSION_CODE, UNCONFINED_VERSION_CODE)

    /**
     * Retrieves the URL for downloading the APK.
     *
     * @return The URL for the APK file or null
     */
    fun apkDownloadUrl(): String? = sharedPreferences.getString(DOWNLOAD_URL, null)

    /**
     * Retrieves the URL for checking update availability.
     *
     * @return The URL for checking updates or null
     */
    fun updateAvailabilityCheckUrl(): String? = sharedPreferences.getString(UPDATE_AVAILABILITY_URL, null)

    /**
     * Retrieves the flag indicating whether the update has started.
     *
     * @return true if the update has started, otherwise false
     */
    fun updateStartedFlag(): Boolean = sharedPreferences.getBoolean(UPDATED_STARTED, false)

    /**
     * Performs saving in SharedPreferences via the provided editor action block.
     *
     * @param editorAction Action to be applied to the SharedPreferences editor
     */
    private fun save(editorAction: SharedPreferences.Editor.() -> Unit) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.editorAction()
        editor.apply()
    }

    companion object {
        /** Default value for the app version if it's not set */
        const val UNCONFINED_VERSION_CODE = -1

        private const val UPDATER_PREFS = "UPDATER_PREFS"
        private const val DOWNLOAD_URL = "DOWNLOAD_URL"
        private const val UPDATE_AVAILABILITY_URL = "UPDATE_AVAILABILITY_URL"
        private const val VERSION_CODE = "VERSION_CODE"
        private const val VERSION_NAME = "VERSION_NAME"
        private const val UPDATED_STARTED = "UPDATED_STARTED"
    }
}
