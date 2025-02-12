package com.earldev.app_update.api

interface UpdateManager {

    fun startUpdate()

    fun updateTokenAndRetry(token: String)

    fun stopUpdate()

    fun onAgreedToGivePermission()

    fun onDeclinedToGivePermission()
}
