package io.serge2nd.taskhero.db

import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.Immutable

@Entity
@Immutable
class Hero(
    @Id val id: Long,
    @ManyToOne val account: Account,
    @ManyToOne(fetch = LAZY) val team: Team,
) {

    val userId get() = account.id

    val userName get() = account.userName
}
