package io.serge2nd.taskhero.etc

import io.serge2nd.taskhero.spi.CoroutineScoped
import kotlinx.coroutines.withContext
import org.springframework.transaction.support.TransactionOperations

suspend inline operator
fun <reified T : CoroutineScoped<T>, R> CoroutineScoped<T>.invoke(
    crossinline body: suspend T.() -> R
): R = withContext(context) { body(this@invoke as T) }

suspend inline operator
fun <reified T : CoroutineScoped<T>, reified R> TransactionOperations.invoke(
    on: CoroutineScoped<T>,
    crossinline body: T.() -> R
): R = withContext(on.context) { execute { body(on as T) } as R }
