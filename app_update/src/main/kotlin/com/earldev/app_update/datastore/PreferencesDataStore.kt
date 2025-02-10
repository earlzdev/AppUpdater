package com.earldev.app_update.datastore

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

internal class PreferencesDataStore @Inject constructor(
    context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(UPDATER_PREFS, Context.MODE_PRIVATE)

    fun saveApkDownloadUrl(url: String) = save {
        putString(DOWNLOAD_URL, url)
    }

    fun saveUpdateAvailabilityCheckUrl(url: String) = save {
        putString(UPDATE_AVAILABILITY_URL, url)
    }

    fun saveActualVersionCode(versionCode: Int) = save {
        putInt(VERSION_CODE, versionCode)
    }

    fun saveActualVersionName(versionName: String) = save {
        putString(VERSION_NAME, versionName)
    }

    fun saveUpdateStartedFlag(updateStarted: Boolean) = save {
        putBoolean(UPDATED_STARTED, updateStarted)
    }

    fun getActualVersionName(): String? = sharedPreferences.getString(VERSION_NAME, null)

    fun getActualVersionCode(): Int = sharedPreferences.getInt(VERSION_CODE, UNCONFINED_VERSION_CODE)

    fun apkDownloadUrl(): String? = sharedPreferences.getString(DOWNLOAD_URL, null)

    fun updateAvailabilityCheckUrl(): String? = sharedPreferences.getString(UPDATE_AVAILABILITY_URL, null)

    fun updateStartedFlag(): Boolean = sharedPreferences.getBoolean(UPDATED_STARTED, false)

    private fun save(editorAction: SharedPreferences.Editor.() -> Unit) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.editorAction()
        editor.apply()
    }

    companion object {
        const val UNCONFINED_VERSION_CODE = -1

        private const val UPDATER_PREFS = "UPDATER_PREFS"

        private const val DOWNLOAD_URL = "DOWNLOAD_URL"
        private const val UPDATE_AVAILABILITY_URL = "UPDATE_AVAILABILITY_URL"
        private const val VERSION_CODE = "VERSION_CODE"
        private const val VERSION_NAME = "VERSION_NAME"
        private const val UPDATED_STARTED = "UPDATED_STARTED"
    }
}