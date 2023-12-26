package io.serge2nd.taskhero.service

import arrow.core.raise.Raise
import arrow.core.raise.either
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.findParameterByName as arg

sealed interface ServiceError {

    val msg: String

    data class Wrong(override val msg: String) : ServiceError

    data class Clash(val prop: String, val value: Any?, override val msg: String = CLASH) : ServiceError

    data class Lack(val prop: String, val value: Any?, override val msg: String = LACK) : ServiceError

    data class Fault(override val msg: String) : ServiceError

    companion object {

        const val CLASH = "already exists or conflicts"

        const val LACK = "not found"

        inline fun <R> serviceScope(body: Raise<ServiceError>.() -> R) = either(body)

        fun Raise<ServiceError>.wrong(msg: String)
        : Nothing = raise(Wrong(msg))

        fun Raise<ServiceError>.clash(prop: String, value: Any?)
        : Nothing = raise(Clash(prop, value))

        fun Raise<ServiceError>.lack(prop: String, value: Any?)
        : Nothing = raise(Lack(prop, value))

        fun Raise<ServiceError>.fault(msg: String)
        : Nothing = raise(Fault(msg))

        inline fun <reified T> Raise<ServiceError>.clash(prop: KProperty1<T, *>, value: Any?)
        : Nothing = raise(create<Clash, _>(prop, value))

        inline fun <reified T> Raise<ServiceError>.lack(prop: KProperty1<T, *>, value: Any?)
        : Nothing = raise(create<Lack, _>(prop, value))

        inline fun <reified E : ServiceError, reified T> create(
            prop: KProperty1<T, *>, value: Any?
        ) = E::class.primaryConstructor!!.run {
            mapOf(
                arg("prop")!! to "${T::class.simpleName}.${prop.name}",
                arg("value")!! to value
            ).let(::callBy)
        }
    }
}
