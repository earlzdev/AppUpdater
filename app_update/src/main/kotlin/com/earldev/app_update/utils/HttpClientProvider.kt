package com.earldev.app_update.utils

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal class HttpClientProvider {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun provide(): OkHttpClient = httpClient
}