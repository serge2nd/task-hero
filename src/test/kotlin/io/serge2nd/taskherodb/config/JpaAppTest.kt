package io.serge2nd.taskherodb.config

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
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootTest(webEnvironment = NONE)
@ContextConfiguration(classes = [
    DataSourceAutoConfiguration::class,
    TransactionManagerCustomizationAutoConfiguration::class,
    HibernateJpaAutoConfiguration::class,
    SqlInitializationAutoConfiguration::class,
    TransactionAutoConfiguration::class,
    DummyHeroes::class
])
@EnableJpaRepositories(basePackages = [ORM_PKG])
@EntityScan(basePackages = [ORM_PKG])
annotation class JpaAppTest {

    companion object { const val ORM_PKG = "io.serge2nd.taskhero.db" }
}
