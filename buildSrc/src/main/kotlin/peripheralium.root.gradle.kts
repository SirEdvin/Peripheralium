import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.function.Consumer

plugins {
    java
}

fun getenv(path: String = ".env"): Map<String, String> {
    val env = hashMapOf<String, String>()

    val file = File(path)
    if (file.exists()) {
        file.readLines().forEach { line ->
            val splitResult = line.split("=")
            if (splitResult.size > 1) {
                env[splitResult[0].trim()] = splitResult[1].trim()
            }
        }
    }

    return env
}

val secretEnv = getenv()
val projectGroup = properties["projectGroup"] ?: "site.siredvin"
val rootProjectDir = projectDir

fun setupSubproject(subproject: Project) {
    subproject.apply(plugin = "kotlin")
    subproject.apply(plugin = "maven-publish")
    subproject.apply(plugin = "com.diffplug.spotless")
    subproject.apply(plugin = "peripheralium.base")
    subproject.apply(plugin = "peripheralium.linting")
    subproject.apply(plugin = "idea")

    subproject.extra["curseforgeKey"] = secretEnv["CURSEFORGE_KEY"] ?: System.getenv("CURSEFORGE_KEY") ?: ""
    subproject.extra["modrinthKey"] = secretEnv["MODRINTH_KEY"] ?: System.getenv("MODRINTH_KEY") ?: ""
    subproject.extra["rootProjectDir"] = rootProjectDir
    subproject.group = projectGroup
}

val x1 = ::setupSubproject

extra["setupSubproject"] = Consumer<Project> {
    setupSubproject(it)
}
