package io.serge2nd.taskhero.db

import io.serge2nd.taskhero.enums.TaskPriority
import io.serge2nd.taskhero.enums.TaskStatus
import io.serge2nd.taskhero.enums.TaskStatus.Open
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import org.hibernate.annotations.ColumnTransformer
import java.time.Duration
import java.time.Duration.ZERO
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

@Entity
class Task(
    @Id @GeneratedValue(generator = TASK_SEQ)
    val id: Long = 0,
    @ColumnTransformer(write = "trim(?)")
    val title: String,
    val details: String,
    val dueDate: LocalDate,
    val cost: Duration,
    @Enumerated(STRING)      val priority: TaskPriority,
    @ManyToOne(fetch = LAZY) val team: Team,
    @ManyToOne(fetch = LAZY) val chief: Hero,
    @ManyToOne(fetch = LAZY) val hero: Hero,
    @Enumerated(STRING)      var status: TaskStatus = Open,
                             var spentTotal: Duration = ZERO,
    @Column(updatable = false) val createdAt: OffsetDateTime = now(),
) {

    @Version // filter by partition key in UPDATE statement
    @Column(name = "team_id", insertable = false, updatable = false)
    private val teamId = 0L

    @ElementCollection
    @CollectionTable(name = "task_spent")
    val log: List<Spent> = ArrayList()

    @Embeddable
    data class Spent(
        @ManyToOne(fetch = LAZY) val hero: Hero,
        val spent: Duration,
        val addedAt: OffsetDateTime = now()
    )
}
