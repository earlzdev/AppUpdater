package com.earldev.app_update.datastore

/**
 * An object for storing data about the available app update.
 * Used for caching version information and update status.
 */
internal object SelfUpdateStore {

    private var remoteVersionCode: Int? = null
    private var remoteVersionName: String? = null
    private var updateAvailable: Boolean? = null
    private var updateStarted: Boolean? = null
    private var checksum: String? = null
    private var bearerToken: String? = null

    /**
     * Sets the remote version name.
     *
     * @param version the version name
     */
    fun setRemoteVersionName(version: String) {
        remoteVersionName = version
    }

    /**
     * Sets the remote version code.
     *
     * @param code the version code
     */
    fun setRemoteVersionCode(code: Int) {
        remoteVersionCode = code
    }

    /**
     * Sets the update availability flag.
     *
     * @param available true if an update is available, false otherwise
     */
    fun setUpdateAvailable(available: Boolean) {
        updateAvailable = available
    }

    /**
     * Sets the update started flag.
     *
     * @param started true if the update has started, false otherwise
     */
    fun setUpdateStarted(started: Boolean) {
        updateStarted = started
    }

    /**
     * Sets the checksum of the remote version.
     *
     * @param cs the checksum
     */
    fun setRemoteVersionChecksum(cs: String) {
        checksum = cs
    }

    /**
     * Sets the authentication token.
     *
     * @param token the bearer token
     */
    fun setToken(token: String) {
        bearerToken = token
    }

    /**
     * Checks if an update is available.
     *
     * @return true if an update is available, otherwise null
     */
    fun updateAvailable(): Boolean? = updateAvailable

    /**
     * Checks if the update has started.
     *
     * @return true if the update has started, otherwise null
     */
    fun updateStarted(): Boolean? = updateStarted

    /**
     * Gets the checksum of the remote version.
     *
     * @return the checksum or null
     */
    fun remoteChecksum(): String? = checksum

    /**
     * Gets the authentication token.
     *
     * @return the bearer token or null
     */
    fun bearerToken(): String? = bearerToken

    /**
     * Clears all stored data.
     */
    fun clear() {
        remoteVersionCode = null
        remoteVersionName = null
        updateAvailable = null
        updateStarted = null
        checksum = null
        bearerToken = null
    }
}
