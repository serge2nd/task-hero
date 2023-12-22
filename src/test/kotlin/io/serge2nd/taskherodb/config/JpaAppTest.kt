package io.serge2nd.taskherodb.config

import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import io.serge2nd.taskhero.DummyHeroes
import io.serge2nd.taskherodb.config.JpaAppTest.Companion.ORM_PKG
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = NONE)
@ContextConfiguration(classes = [
    DataSourceAutoConfiguration::class,
    HibernateJpaAutoConfiguration::class,
    KotlinJdslAutoConfiguration::class,
    SqlInitializationAutoConfiguration::class,
    TransactionAutoConfiguration::class,
    TransactionManagerCustomizationAutoConfiguration::class,
    JpaAppTest.Companion::class,
    DummyHeroes::class
])
annotation class JpaAppTest {

    @EnableJpaRepositories(ORM_PKG) @EntityScan(ORM_PKG)
    companion object { const val ORM_PKG = "io.serge2nd.taskhero.db" }
}
