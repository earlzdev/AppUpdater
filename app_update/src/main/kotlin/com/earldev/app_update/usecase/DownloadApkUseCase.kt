package com.earldev.app_update.usecase

import android.util.Log
import androidx.annotation.WorkerThread
import com.earldev.app_update.api.models.UnauthorizedException
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.utils.HttpClientProvider
import com.earldev.app_update.utils.SelfUpdateLog
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/**
 * Interface for downloading the APK file.
 */
internal interface DownloadApkUseCase {

    /**
     * Downloads the APK file.
     *
     * @return `true` if the download was successful, `false` otherwise.
     * @throws IllegalArgumentException if invalid parameters are provided.
     * @throws IllegalStateException if the download URL is missing.
     */
    @WorkerThread
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun download(): Boolean
}

/**
 * Implementation of [DownloadApkUseCase] for downloading an APK file via HTTP.
 *
 * @param httpClientProvider The provider for the HTTP client.
 * @property dataStore The data store that contains the download URL.
 * @property saveAndDeleteApkUseCase Use-case for saving the downloaded APK.
 */
internal class DownloadApkUseCaseImpl @Inject constructor(
    httpClientProvider: HttpClientProvider,
    private val dataStore: PreferencesDataStore,
    private val saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase,
) : DownloadApkUseCase {

    private val httpClient: OkHttpClient = httpClientProvider.provide()

    /**
     * Executes the APK download.
     *
     * @return `true` if the file was successfully downloaded and saved, `false` otherwise.
     * @throws IllegalStateException if the download URL is missing.
     * @throws UnauthorizedException if the server returns a 401 (unauthorized) response.
     */
    override fun download(): Boolean {
        SelfUpdateLog.logInfo("Start downloading apk")

        Log.d("SELF", "download: token ${SelfUpdateStore.bearerToken()}")

        val url: String = dataStore.apkDownloadUrl()
            ?: throw IllegalStateException("No URL for downloading APK")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${SelfUpdateStore.bearerToken()}")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (response.code == 401) {
                SelfUpdateLog.logError("Unauthorized request for APK download")
                throw UnauthorizedException()
            }

            if (!response.isSuccessful) {
                SelfUpdateLog.logInfo("Unsuccessful APK download: $response")
                return false
            }

            return saveAndDeleteApkUseCase.save(inputStream = response.body?.byteStream())
        }
    }
}