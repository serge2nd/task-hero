package io.serge2nd.taskhero

import io.serge2nd.taskhero.db.*
import jakarta.persistence.EntityManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionOperations

@Component
@Profile("dev")
class DummyHeroes(
    private val em: EntityManager,
    private val tx: TransactionOperations
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) = tx.executeWithoutResult {
        var id = 0L
        val users = listOf("alex", "serge", "vi").associateWith { em.merge(Account(++id, it)) }
        val teams = listOf("police", "bandits").associateWith { em.merge(Team(++id, it)) }
        em.merge(Hero(++id, users["alex"]!!, teams["police"]!!))
        em.merge(Hero(++id, users["serge"]!!, teams["bandits"]!!))
        em.merge(Hero(++id, users["vi"]!!, teams["bandits"]!!))
    }
}
