package io.serge2nd.taskhero.spi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.slf4j.MDCContext

interface IoScoped<T : IoScoped<T>> : CoroutineScoped<T> {

    override val context get() = Dispatchers.IO + MDCContext()
}
