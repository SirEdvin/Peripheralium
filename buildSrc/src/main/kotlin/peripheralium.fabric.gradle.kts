import site.siredvin.peripheralium.gradle.ConfigureFabric
import site.siredvin.peripheralium.gradle.ConfigureProject

plugins {
    java
    id("fabric-loom")
}

fun configureFabric(targetProject: Project, accessWidener: File, commonProjectName: String) {
    val minecraftVersion: String by targetProject.extra
    val modBaseName: String by targetProject.extra

    targetProject.dependencies {
        minecraft("com.mojang:minecraft:$minecraftVersion")
        mappings(loom.officialMojangMappings())
        implementation(project(":$commonProjectName")) {
            exclude("cc.tweaked")
        }
    }

    targetProject.loom {
        accessWidenerPath.set(accessWidener)
        runs {
            named("client") {
                configName = "Fabric Client"
            }
            named("server") {
                configName = "Fabric Server"
            }
            create("data") {
                client()
                vmArg("-Dfabric-api.datagen")
                vmArg("-Dfabric-api.datagen.modid=$modBaseName")
                vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
                vmArg("-Dfabric-api.datagen.strict-validation")
            }
        }
    }

    targetProject.tasks {
        processResources {
            from(project(":$commonProjectName").sourceSets.main.get().resources)
            inputs.property("version", targetProject.version)
            filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to targetProject.version)) }
            exclude(".cache")
        }
        withType<JavaCompile> {
            if (this.name == "compileJava") {
                source(project(":$commonProjectName").sourceSets.main.get().allSource)
            }
        }
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            if (this.name == "compileKotlin") {
                source(project(":$commonProjectName").sourceSets.main.get().allSource)
            }
        }
    }
}

class FabricShakingExtension(private val targetProject: Project) {
    val commonProjectName: Property<String> = targetProject.objects.property(String::class.java)
    val accessWidener: Property<File> = targetProject.objects.property(File::class.java)

    fun shake() {
        configureFabric(targetProject, accessWidener.get(), commonProjectName.get())
    }
}

val fabricShaking = FabricShakingExtension(project)
project.extensions.add("fabricShaking", fabricShaking)
