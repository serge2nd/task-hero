package io.serge2nd.taskhero.db

import io.serge2nd.taskhero.spi.IoScoped
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.Repository

interface Accounts : Repository<Account, Long>, IoScoped<Accounts> {

    @EntityGraph(attributePaths = ["heroes"])
    fun findByUserName(userName: String): Account?
}
