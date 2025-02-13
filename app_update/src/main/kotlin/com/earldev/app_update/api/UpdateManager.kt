package com.earldev.app_update.api

/**
 * Interface for managing the update process.
 */
interface UpdateManager {

    /**
     * Starts the update process.
     */
    fun startUpdate()

    /**
     * Updates the JWT token and continues the update process. Should be called when
     * receiving [UnauthorizedException].
     *
     * @param token the updated JWT token
     */
    fun updateTokenAndRetry(token: String)

    /**
     * Stops the update process.
     */
    fun stopUpdate()

    /**
     * Called when the user agrees to grant permission for installing packages.
     */
    fun onAgreedToGivePermission()

    /**
     * Called when the user declines to grant permission for installing packages.
     */
    fun onDeclinedToGivePermission()
}
