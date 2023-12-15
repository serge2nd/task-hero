package io.serge2nd.taskhero.db

import io.serge2nd.taskhero.spi.IoScoped
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.OffsetDateTime

interface Tasks : CrudRepository<Task, Long>, IoScoped<Tasks> {

    @Query("""
        from Task t join fetch t.team join fetch t.chief join fetch t.hero
        join fetch t.chief.account join fetch t.hero.account
        left join fetch t.log log left join fetch log.hero
        where t.team = :team and t.title = trim(:title)
          and log.hero is null or log.addedAt > t.createdAt and log.addedAt < instant
    """)
    fun findByTeamAndTitle(team: Team, title: String): Task?

    @EntityGraph(attributePaths = ["team", "chief", "hero"], type = LOAD)
    fun findByTeamTitleIn(teams: Collection<String>): List<Task>

    @Query("""
        from Task t join fetch t.team join fetch t.log log
        where t.team = :#{#hero.team} and log.hero = :hero
          and log.addedAt >= greatest(:logFrom, t.createdAt) and log.addedAt < instant
    """)
    fun findTaskLog(hero: Hero, logFrom: OffsetDateTime): List<Task>
}
