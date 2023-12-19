package io.serge2nd.taskhero.db

import io.serge2nd.taskhero.enums.TaskStatus
import io.serge2nd.taskhero.spi.IoScoped
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

interface Tasks : CrudRepository<Task, Long>, IoScoped<Tasks> {

    fun findByTeamAndTitle(team: Team, title: String): Task?

    @EntityGraph(attributePaths = ["chief.account", "hero.account"])
    fun findByTeamAndTitleAndStatus(team: Team, title: String, status: TaskStatus): Task?

    @Query("""
        from Task t join fetch t.team
        join fetch t.chief join fetch t.chief.account
        join fetch t.hero join fetch t.hero.account
        left join fetch t.log log left join fetch log.hero
        where t.team = :team and t.title = trim(:title)
          and log.hero is null or log.addedAt > t.createdAt and log.addedAt < instant
    """)
    fun findByTeamAndTitleHeavy(team: Team, title: String): Task?

    @Modifying
    @Query("""insert into task_spent(task_id, hero_id, spent, added_at)
        values(:#{#task.id}, :#{#e.hero.id}, :#{#e.spent.toString()}, :#{#e.addedAt})
    """, nativeQuery = true)
    fun addTaskSpent(task: Task, e: Task.Spent)

    @Transactional
    fun Task.logSpent(spent: Task.Spent) {
        addTaskSpent(this, spent)
        save(apply { spentTotal += spent.spent })
    }

    @EntityGraph(attributePaths = ["team", "chief.account", "hero.account"])
    fun findByTeamTitleIn(teams: Collection<String>): List<Task>

    @Query("""
        from Task t join fetch t.team join fetch t.log log
        where log.hero in :heroes and log.hero.team = t.team
          and log.addedAt >= greatest(:logFrom, t.createdAt) and log.addedAt < instant
    """)
    fun findTaskLog(heroes: Collection<Hero>, logFrom: OffsetDateTime): List<Task>
}
