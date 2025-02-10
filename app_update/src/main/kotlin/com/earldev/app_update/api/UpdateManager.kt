package com.earldev.app_update.api

interface UpdateManager {

    fun startUpdate()

    fun stopUpdate()

    fun onAgreedToGivePermission()

    fun onDeclinedToGivePermission()
}
