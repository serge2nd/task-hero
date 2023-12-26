package io.serge2nd.taskherodb

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import io.kotest.core.spec.style.FunSpec
import jakarta.persistence.EntityManager
import kotlin.reflect.KProperty1

abstract class JpaTestSpec(body: JpaTestSpec.() -> Unit)
: FunSpec(), EntityManager, KotlinJdslJpqlExecutor { init { body() } }

inline fun <reified E : Any> Jpql.en() = entity(E::class)

inline fun <reified E : Any> KotlinJdslJpqlExecutor.all(
    vararg load: KProperty1<out Any, *>,
    crossinline p: Jpql.() -> Predicatable? = { null }
): List<E> = findAll {
    select(en<E>()).from(en<E>(), *load.map { leftFetchJoin(it) }.toTypedArray()).where(p())
}.filterNotNull()

context(KotlinJdslJpqlExecutor) inline
fun <reified E : Any> KProperty1<E, *>.eq(v: Any?, vararg load: KProperty1<out Any, *>)
: E = all<E>(*load) { path(this@eq).eq(v) }.first()

fun EntityManager.reset() { flush(); clear() }
