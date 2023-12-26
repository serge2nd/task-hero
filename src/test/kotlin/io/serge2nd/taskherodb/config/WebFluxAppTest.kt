package io.serge2nd.taskherodb.config

import com.ninjasquad.springmockk.MockkBean
import io.serge2nd.taskhero.api.ErrorHandler
import io.serge2nd.taskhero.config.CoreConfig
import io.serge2nd.taskhero.service.TaskService
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [
    JacksonAutoConfiguration::class,
    CodecsAutoConfiguration::class,
    ValidationAutoConfiguration::class,
    WebFluxAutoConfiguration::class,
    WebTestClientAutoConfiguration::class,
    CoreConfig::class,
    WebFluxAppTest.Companion::class,
])
annotation class WebFluxAppTest {

    @ComponentScan(basePackageClasses = [ErrorHandler::class])
    @MockkBean(TaskService::class)
    companion object
}
