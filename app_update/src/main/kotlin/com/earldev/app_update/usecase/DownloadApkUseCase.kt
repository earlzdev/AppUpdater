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

internal interface DownloadApkUseCase {

    @WorkerThread
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun download(): Boolean
}

internal class DownloadApkUseCaseImpl @Inject constructor(
    httpClientProvider: HttpClientProvider,
    private val dataStore: PreferencesDataStore,
    private val saveAndDeleteApkUseCase: SaveAndDeleteApkUseCase,
) : DownloadApkUseCase {

    private val httpClient: OkHttpClient = httpClientProvider.provide()

    override fun download(): Boolean {
        SelfUpdateLog.logInfo("Start downloading apk")

        Log.d("SELF", "download: token ${SelfUpdateStore.bearerToken()}")

        val url: String = dataStore.apkDownloadUrl()
            ?: throw IllegalStateException("No url for download apk")
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${SelfUpdateStore.bearerToken()}")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (response.code == 401) {
                SelfUpdateLog.logError("Unauthorized request for download apk request")
                throw UnauthorizedException()
            }

            if (!response.isSuccessful) {
                SelfUpdateLog.logInfo("Unsuccessful load apk: $response")
                return false
            }

            return saveAndDeleteApkUseCase.save(inputStream = response.body?.byteStream())
        }
    }
}