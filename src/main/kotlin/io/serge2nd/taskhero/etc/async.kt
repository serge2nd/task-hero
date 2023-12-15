package io.serge2nd.taskhero.etc

import io.serge2nd.taskhero.spi.CoroutineScoped
import kotlinx.coroutines.withContext

suspend inline operator
fun <reified T : CoroutineScoped<T>, R> CoroutineScoped<T>.invoke(
    crossinline body: suspend T.() -> R
): R = withContext(context) { body(this@invoke as T) }
