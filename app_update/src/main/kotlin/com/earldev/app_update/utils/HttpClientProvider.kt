package com.earldev.app_update.utils

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Initializes and provides an instance of [OkHttpClient] for use in various places.
 *
 */
internal class HttpClientProvider {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Returns an instance of [OkHttpClient].
     */
    fun provide(): OkHttpClient = httpClient
}