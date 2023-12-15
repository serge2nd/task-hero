package io.serge2nd.taskhero.db

import io.serge2nd.taskhero.spi.IoScoped
import org.springframework.data.repository.Repository

interface Teams : Repository<Team, Long>, IoScoped<Teams> {

    fun findByTitle(title: String): Team?
}
