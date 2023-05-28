import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.function.BiConsumer
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


fun setupSubprojectExternal(subproject: Project) {
    subproject.apply(plugin = "maven-publish")
    subproject.apply(plugin = "com.diffplug.spotless")
    subproject.apply(plugin = "peripheralium.base")
    subproject.apply(plugin = "peripheralium.linting")
    subproject.apply(plugin = "idea")
    if (subprojectShaking.withKotlin.get())
        subproject.apply(plugin = "kotlin")

    subproject.extra["curseforgeKey"] = secretEnv["CURSEFORGE_KEY"] ?: System.getenv("CURSEFORGE_KEY") ?: ""
    subproject.extra["modrinthKey"] = secretEnv["MODRINTH_KEY"] ?: System.getenv("MODRINTH_KEY") ?: ""
    subproject.extra["rootProjectDir"] = rootProjectDir
    subproject.group = projectGroup

    if (subprojectShaking.withKotlin.get()) {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${subprojectShaking.kotlinVersion.get()}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${subprojectShaking.kotlinCoroutinesVersion.get()}")
            implementation("org.jetbrains.kotlinx:atomicfu-jvm:${subprojectShaking.kotlinAtomicfuVersion.get()}")
        }
    }
}

class SubProjectShakingExtension(targetProject: Project) {
    val withKotlin: Property<Boolean> = targetProject.objects.property(Boolean::class.java)
    val kotlinVersion: Property<String> = targetProject.objects.property(String::class.java)
    val kotlinCoroutinesVersion: Property<String> = targetProject.objects.property(String::class.java)
    val kotlinAtomicfuVersion: Property<String> = targetProject.objects.property(String::class.java)

    init {
        kotlinVersion.convention("1.8.21")
        kotlinCoroutinesVersion.convention("1.6.4")
        kotlinAtomicfuVersion.convention("0.20.2")
    }

    fun setupSubproject(subproject: Project) {
        setupSubprojectExternal(subproject)
    }
}

val subprojectShaking = SubProjectShakingExtension(project)
project.extensions.add("subprojectShaking", subprojectShaking)