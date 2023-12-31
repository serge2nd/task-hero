package io.serge2nd.taskherodb

import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec
import org.springframework.test.web.reactive.server.WebTestClient.UriSpec
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.util.UriBuilder
import java.net.URI

inline fun <S : RequestHeadersSpec<*>> UriSpec<S>.url(
    path: String,
    vararg params: Pair<String, Any?>,
    crossinline then: UriBuilder.() -> URI = { build() }
) = uri {
    it.path(path)
    for ((k, v) in params) when (v) {
        is Array<*> -> it.queryParam(k, *v)
        is Collection<*> -> it.queryParam(k, v)
        else -> it.queryParam(k, v)
    }
    it.then()
}

inline fun <reified B : Any> RequestHeadersSpec<*>.`as`() = exchange().expectBody<B>().returnResult()

inline fun <reified E : Any> RequestHeadersSpec<*>.asList() = `as`<List<E>>()
