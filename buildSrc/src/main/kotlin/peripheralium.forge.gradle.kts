plugins {
    `java`
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
    id("org.spongepowered.mixin")
}


fun configureForge(targetProject: Project, useAT: Boolean, commonProjectName: String, useMixins: Boolean, versionMappings: Map<String, String>) {

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

    if (useMixins) {
        targetProject.mixin {
            add(targetProject.sourceSets.main.get(), "$modBaseName.refmap.json")
            config("$modBaseName.mixins.json")
        }
        targetProject.dependencies {
            annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
        }
    }

    targetProject.tasks {
        val extractedLibs = targetProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val forgeVersion = extractedLibs.findVersion("forge").get()

        processResources {
            from(project(":${commonProjectName}").sourceSets.main.get().resources)

            inputs.property("version", targetProject.version)
            inputs.property("forgeVersion", forgeVersion)

            filesMatching("META-INF/mods.toml") {
                expand(
                    mapOf(
                        "forgeVersion" to forgeVersion,
                        "file" to mapOf("jarVersion" to targetProject.version),
                    ),
                )
                expand(versionMappings.entries.associate { "${it.key}Version" to extractedLibs.findVersion(it.value).get() })
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
    val useMixins: Property<Boolean> = targetProject.objects.property(Boolean::class.java)
    val extraVersionMappings: MapProperty<String, String> = targetProject.objects.mapProperty(String::class.java, String::class.java)

    fun shake() {
        useMixins.convention(false)
        extraVersionMappings.convention(emptyMap())
        configureForge(targetProject, useAT.get(), commonProjectName.get(), useMixins.get(), extraVersionMappings.get())
    }
}

val forgeShaking: ForgeShakingExtension = ForgeShakingExtension(project)
project.extensions.add("forgeShaking", forgeShaking)

repositories {
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
