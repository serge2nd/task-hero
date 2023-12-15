package io.serge2nd.taskhero.db

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Immutable

@Entity
@Immutable
class Account(
    @Id val id: Long,
    val userName: String,
) {

    @OneToMany(mappedBy = "account")
    val heroes: List<Hero> = ArrayList()

    val heroesByTeamId get() = heroes.associateBy { it.team.id }
}
