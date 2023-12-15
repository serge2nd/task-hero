package io.serge2nd.taskhero

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@ConfigurationPropertiesScan
@SpringBootApplication(
    scanBasePackages = ["io.serge2nd"],
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
private class App

fun main() { runApplication<App>() }
