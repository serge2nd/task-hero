package io.serge2nd.taskherodb.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringAutowireConstructorExtension
import io.kotest.extensions.spring.SpringExtension
import io.serge2nd.taskherodb.setFinal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestProject : AbstractProjectConfig() {

    override fun extensions() = listOf(SpringExtension, SpringAutowireConstructorExtension)

    override suspend fun beforeProject() {
        setFinal<Dispatchers>("IO", BlockingDispatcher)
    }

    private object BlockingDispatcher : CoroutineDispatcher() {

        override fun dispatch(context: CoroutineContext, block: Runnable) = error("denied")

        override fun isDispatchNeeded(context: CoroutineContext) = false

        override operator fun plus(context: CoroutineContext) = this
    }
}
