package com.earldev.app_update.usecase

import androidx.annotation.WorkerThread
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.utils.HttpClientProvider
import com.earldev.app_update.utils.SelfUpdateLog
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

internal interface DownloadApkUseCase {

    @WorkerThread
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun download()
}

internal class DownloadApkUseCaseImpl @Inject constructor(
    httpClientProvider: HttpClientProvider,
    private val dataStore: PreferencesDataStore,
    private val saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase,
) : DownloadApkUseCase {

    private val httpClient: OkHttpClient = httpClientProvider.provide()

    override fun download() {
        SelfUpdateLog.logInfo("Start downloading apk")

        val url: String = dataStore.apkDownloadUrl()
            ?: throw IllegalStateException("No url for download apk")
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                SelfUpdateLog.logInfo("Unsuccessful load apk: $response")
                return
            }

            saveAndDeleteApkUseCase.save(inputStream = response.body?.byteStream())
        }
    }
}