package com.earldev.app_update.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

internal interface CoroutineDispatchers {
    val io: CoroutineDispatcher
}

internal class CoroutineDispatchersImpl @Inject constructor() : CoroutineDispatchers {

    override val io: CoroutineDispatcher = Dispatchers.IO
}