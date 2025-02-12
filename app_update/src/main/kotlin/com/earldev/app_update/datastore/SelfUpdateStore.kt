package com.earldev.app_update.datastore

internal object SelfUpdateStore {

    private var remoteVersionCode: Int? = null
    private var remoteVersionName: String? = null
    private var updateAvailable: Boolean? = null
    private var updateStarted: Boolean? = null
    private var chechsum: String? = null
    private var bearerToken: String? = null

    fun setRemoteVersionName(version: String) {
        remoteVersionName = version
    }

    fun setRemoteVersionCode(code: Int) {
        remoteVersionCode = code
    }

    fun setUpdateAvailable(available: Boolean) {
        updateAvailable = available
    }

    fun setUpdateStarted(started: Boolean) {
        updateStarted = started
    }

    fun setRemoteVersionChecksum(cs: String) {
        chechsum = cs
    }

    fun setToken(token: String) {
        bearerToken = token
    }

    fun updateAvailable(): Boolean? = updateAvailable

    fun updateStarted(): Boolean? = updateStarted

    fun remoteChecksum(): String? = chechsum

    fun bearerToken(): String? = bearerToken

    fun clear() {
        remoteVersionCode = null
        remoteVersionName = null
        updateAvailable = null
        updateStarted = null
        chechsum = null
    }
}