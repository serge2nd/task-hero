package io.serge2nd.taskhero.service

import arrow.core.raise.Raise
import arrow.core.raise.either
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

sealed class ServiceError(val msg: String) {

    class BadRequest(msg: String) : ServiceError(msg)

    class Conflict(val prop: String, val value: String?) : ServiceError("already exists or conflicts")

    class NotFound(val prop: String, val value: String?) : ServiceError("not found")

    companion object {

        inline fun <R> serviceScope(body: Raise<ServiceError>.() -> R) = either(body)

        fun Raise<ServiceError>.badRequest(msg: String)
        : Nothing = raise(BadRequest(msg))

        fun Raise<ServiceError>.conflict(prop: String, value: String?)
        : Nothing = raise(Conflict(prop, value))

        fun Raise<ServiceError>.notFound(prop: String, value: String?)
        : Nothing = raise(NotFound(prop, value))

        inline fun <reified T> Raise<ServiceError>.conflict(prop: KProperty1<T, *>, value: String?)
        : Nothing = raise(create<Conflict, _>(prop, value))

        inline fun <reified T> Raise<ServiceError>.notFound(prop: KProperty1<T, *>, value: String?)
        : Nothing = raise(create<NotFound, _>(prop, value))

        inline fun <reified E : ServiceError, reified T> create(prop: KProperty1<T, *>, value: String?) =
            E::class.primaryConstructor!!.call("${T::class.simpleName}.${prop.name}", value)
    }
}
