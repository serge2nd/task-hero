package io.serge2nd.taskhero.spi

import kotlin.coroutines.CoroutineContext

interface CoroutineScoped<T : CoroutineScoped<T>> {

    val context: CoroutineContext
}
