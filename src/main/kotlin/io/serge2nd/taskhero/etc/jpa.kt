package io.serge2nd.taskhero.etc

import jakarta.persistence.EntityManagerFactory
import org.hibernate.proxy.HibernateProxy

inline fun <reified T> EntityManagerFactory.unwrap(): T = unwrap(T::class.java)

@Suppress("UNCHECKED_CAST")
val <E : Any> E.entityClass: Class<out E> get() = when (this) {
    is HibernateProxy -> Class.forName(hibernateLazyInitializer.entityName) as Class<E>
    else -> javaClass
}
