import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

rootProject.name = "task-hero"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = FAIL_ON_PROJECT_REPOS
    repositories {
        mavenLocal()
        mavenCentral()
    }
    on("io.mockk:mockk-jvm") { drop("junit") }
    on("org.springframework.boot:spring-boot-starter-test") { drop("org.mockito") }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

fun DependencyResolutionManagement.on(id: String, a: Action<ComponentMetadataDetails>) =
    components.withModule(id, a)
fun ComponentMetadataDetails.drop(vararg groups: String) =
    allVariants { withDependencies { removeAll { it.group in groups } } }
