plugins {
    `java-library`
    kotlin("jvm") version "1.9.21"
    kotlin("kapt") version "1.9.21"
    kotlin("plugin.jpa") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "io.serge2nd"
version = "0.1.0-SNAPSHOT"

dependencies {
    //region Kotlin stuff
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
    implementation("io.arrow-kt:arrow-core:1.2.1")
    implementation("io.github.oshai:kotlin-logging:5.1.1") //endregion

    //region Data
    implementation("io.hypersistence:hypersistence-utils-hibernate-62:3.6.1")
    implementation("org.flywaydb:flyway-core")
    kapt("org.hibernate:hibernate-jpamodelgen:6.3.2.Final")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") //endregion

    //region Web
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux") //endregion

    //region Test
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation(platform("io.kotest:kotest-bom:5.8.0"))
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.springframework.boot:spring-boot-starter-test") //endregion
}

kotlin {
    jvmToolchain(17)
    compilerOptions.freeCompilerArgs = listOf(
        "-Xemit-jvm-type-annotations",
        "-Xjsr305=strict",
        "-Xjvm-default=all"
    )
    for (pkg in setOf("jakarta.persistence", "javax.persistence"))
        allOpen.annotations("$pkg.Entity", "$pkg.Embeddable", "$pkg.MappedSuperclass")
    noArg.invokeInitializers = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    jvmArgs = listOf(
        "-Dspring.profiles.active=dev,test",
        "-Duser.language=en",
        "-Duser.timezone=UTC",
        "-Dkotest.framework.config.fqn=io.serge2nd.taskherodb.config.TestProject",
        "-Dkotest.framework.classpath.scanning.autoscan.disable=true",
        "-Dkotest.framework.classpath.scanning.config.disable=true",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
    )
}

tasks.bootJar {
    archiveVersion = ""
}
