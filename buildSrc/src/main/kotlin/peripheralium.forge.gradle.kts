import site.siredvin.peripheralium.gradle.ConfigureProject

plugins {
    `java`
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
}


fun configureForge(targetProject: Project, useAT: Boolean, commonProjectName: String) {

    val minecraftVersion: String by targetProject.extra
    val modBaseName: String by targetProject.extra

    targetProject.minecraft {
        val extractedLibs = targetProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        // So, this exists mostly because mapping for 1.19.4 forge are not complete (?)
        mappings(
            "parchment",
            "${extractedLibs.findVersion("parchmentMc").get()}-${
                extractedLibs.findVersion("parchment").get()
            }-$minecraftVersion"
        )

        if (useAT) {
            accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
        }

        runs {
            all {
                property("forge.logging.markers", "REGISTRIES")
                property("forge.logging.console.level", "debug")
                property("mixin.env.remapRefMap", "true")
                property("mixin.env.refMapRemappingFile", "${targetProject.projectDir}/build/createSrgToMcp/output.srg")
            }

            val client by registering {
                workingDirectory(file("run"))
            }

            val server by registering {
                workingDirectory(file("run/server"))
                arg("--nogui")
            }

            val data by registering {
                workingDirectory(file("run"))
                args(
                    "--mod", modBaseName, "--all",
                    "--output", file("src/generated/resources/"),
                    "--existing", project(":core").file("src/main/resources/"),
                    "--existing", file("src/main/resources/"),
                )
            }
        }
    }

    targetProject.dependencies {
        val extractedLibs = targetProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        minecraft("net.minecraftforge:forge:$minecraftVersion-${extractedLibs.findVersion("forge").get()}")

        compileOnly(project(":${commonProjectName}")) {
            exclude("cc.tweaked")
            exclude("fuzs.forgeconfigapiport")
        }
    }

    targetProject.tasks {
        val extractedLibs = targetProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val forgeVersion = extractedLibs.findVersion("forge").get()
        val computercraftVersion = extractedLibs.findVersion("cc-tweaked").get()

        processResources {
            from(project(":${commonProjectName}").sourceSets.main.get().resources)

            inputs.property("version", targetProject.version)
            inputs.property("forgeVersion", forgeVersion)
            inputs.property("computercraftVersion", computercraftVersion)

            filesMatching("META-INF/mods.toml") {
                expand(
                    mapOf(
                        "forgeVersion" to forgeVersion,
                        "file" to mapOf("jarVersion" to targetProject.version),
                        "computercraftVersion" to computercraftVersion,
                    ),
                )
            }
            exclude(".cache")
        }
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            if (name == "compileKotlin") {
                source(project(":${commonProjectName}").sourceSets.main.get().allSource)
            }
        }
        withType<JavaCompile> {
            if (name == "compileJava") {
                source(project(":${commonProjectName}").sourceSets.main.get().allSource)
            }
        }
    }
}

class ForgeShakingExtension(private val targetProject: Project) {
    val commonProjectName: Property<String> = targetProject.objects.property(String::class.java)
    val useAT: Property<Boolean> = targetProject.objects.property(Boolean::class.java)

    fun shake() {
        configureForge(targetProject, useAT.get(), commonProjectName.get())
    }
}

val forgeShaking: ForgeShakingExtension = ForgeShakingExtension(project)
project.extensions.add("forgeShaking", forgeShaking)

val modVersion: String by project.extra
val minecraftVersion: String by project.extra
val modBaseName: String by project.extra
val configureProject: ConfigureProject by extra
configureProject.configure(modBaseName, modVersion, "forge", minecraftVersion)

repositories {
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
}

tasks.jar {
    finalizedBy("reobfJar")
    archiveClassifier.set("")
}
