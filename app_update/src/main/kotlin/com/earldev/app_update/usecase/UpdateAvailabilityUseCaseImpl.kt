package com.earldev.app_update.usecase

import com.earldev.app_update.AppVersionsComparator
import com.earldev.app_update.api.UpdateAvailabilityUseCase
import com.earldev.app_update.api.models.UnauthorizedException
import com.earldev.app_update.datastore.PreferencesDataStore
import com.earldev.app_update.datastore.PreferencesDataStore.Companion.UNCONFINED_VERSION_CODE
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.models.AppVersionInfo
import com.earldev.app_update.utils.CoroutineDispatchers
import com.earldev.app_update.utils.HttpClientProvider
import com.earldev.app_update.utils.SelfUpdateLog
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

internal class UpdateAvailabilityUseCaseImpl @Inject constructor(
    httpClientProvider: HttpClientProvider,
    private val dataStore: PreferencesDataStore,
    private val versionsComparator: AppVersionsComparator,
    private val coroutineDispatchers: CoroutineDispatchers,
) : UpdateAvailabilityUseCase {

    private val httpClient: OkHttpClient = httpClientProvider.provide()

    override suspend fun updateAvailable(): Result<Boolean> = runCatching {
        withContext(coroutineDispatchers.io) {
            val remoteVersion = fetchRemoteVersion()
            val actualVersion = actualVersion()

            val updateAvailable = versionsComparator.needUpdate(actualVersion, remoteVersion)

            val checksum = requireNotNull(remoteVersion.checksum) {
                "Remote version checksum is null"
            }
            with(SelfUpdateStore) {
                setRemoteVersionCode(remoteVersion.versionCode)
                setRemoteVersionName(remoteVersion.versionName)
                setRemoteVersionChecksum(checksum)
                setUpdateAvailable(updateAvailable)
            }

            updateAvailable
        }
    }

    private fun fetchRemoteVersion(): AppVersionInfo {
        val url: String = dataStore.updateAvailabilityCheckUrl()
            ?: throw IllegalStateException("No url for check remove version")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${SelfUpdateStore.bearerToken()}")
            .get()
            .build()

        return httpClient.newCall(request).execute().use { response ->
            if (response.code == 401) {
                SelfUpdateLog.logError("Unauthorized request for check available update")
                throw UnauthorizedException()
            }

            val responseBody = requireNotNull(response.body?.string())
            Json.decodeFromString<AppVersionInfo>(responseBody)
        }
    }

    private fun actualVersion(): AppVersionInfo {
        val actualVersionCode = dataStore.getActualVersionCode().takeIf { it != UNCONFINED_VERSION_CODE }
            ?: throw IllegalStateException("No actual version code")
        val actualVersionName = requireNotNull(dataStore.getActualVersionName()) {
            "No actual version name"
        }

        return AppVersionInfo(
            versionCode = actualVersionCode,
            versionName = actualVersionName,
        )
    }
}