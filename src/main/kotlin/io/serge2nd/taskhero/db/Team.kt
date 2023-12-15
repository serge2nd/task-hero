package io.serge2nd.taskhero.db

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Immutable

@Entity
@Immutable
class Team(
    @Id val id: Long,
    val title: String,
)
