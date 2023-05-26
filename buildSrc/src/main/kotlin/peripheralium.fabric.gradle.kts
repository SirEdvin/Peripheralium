import site.siredvin.peripheralium.gradle.ConfigureFabric
import site.siredvin.peripheralium.gradle.ConfigureProject

plugins {
    java
    id("fabric-loom")
}

extra["configureMinecraft"] = ConfigureFabric { modBaseName, modVersion, minecraftVersion, accessWidener, commonProjectName ->
    val configureProject: ConfigureProject by extra
    configureProject.configure(modBaseName, modVersion, "fabric", minecraftVersion)
    dependencies {
        minecraft("com.mojang:minecraft:$minecraftVersion")
        mappings(loom.officialMojangMappings())
        implementation(project(":$commonProjectName")) {
            exclude("cc.tweaked")
        }
    }

    loom {
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

    tasks {
        processResources {
            from(project(":$commonProjectName").sourceSets.main.get().resources)
            inputs.property("version", project.version)
            filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
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