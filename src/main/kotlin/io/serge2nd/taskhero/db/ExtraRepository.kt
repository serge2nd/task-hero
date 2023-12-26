package io.serge2nd.taskhero.db

interface ExtraRepository {

    val <E : Any> E.ref: E
}
