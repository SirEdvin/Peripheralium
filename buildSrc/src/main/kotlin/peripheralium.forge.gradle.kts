import site.siredvin.peripheralium.gradle.ConfigureProject

plugins {
    `java`
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

abstract class ForgeShackingExtension {
    abstract val commonProjectName: Property<String>
    abstract val useKotlin: Property<Boolean>
    abstract val useAT: Property<Boolean>
}

val forgeShacking = project.extensions.create("forgeShacking", ForgeShackingExtension::class.java)
forgeShacking.commonProjectName.convention("core")
forgeShacking.useKotlin.convention(true)
forgeShacking.useAT.convention(false)

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

minecraft {
    val extractedLibs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    // So, this exists mostly because mapping for 1.19.4 forge are not complete (?)
    mappings("parchment", "${extractedLibs.findVersion("parchmentMc").get()}-${extractedLibs.findVersion("parchment").get()}-$minecraftVersion")

    if (forgeShacking.useAT.get()) {
        accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    }

    runs {
        all {
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")
            forceExit = false
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

dependencies {
    val extractedLibs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    minecraft("net.minecraftforge:forge:$minecraftVersion-${extractedLibs.findVersion("forge").get()}")

    compileOnly(project(":${forgeShacking.commonProjectName.get()}")) {
        exclude("cc.tweaked")
        exclude("fuzs.forgeconfigapiport")
    }
}

tasks {
    val extractedLibs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    val forgeVersion = extractedLibs.findVersion("forge").get()
    val computercraftVersion = extractedLibs.findVersion("cc-tweaked").get()

    processResources {
        from(project(":${forgeShacking.commonProjectName.get()}").sourceSets.main.get().resources)

        inputs.property("version", project.version)
        inputs.property("forgeVersion", forgeVersion)
        inputs.property("computercraftVersion", computercraftVersion)

        filesMatching("META-INF/mods.toml") {
            expand(
                mapOf(
                    "forgeVersion" to forgeVersion,
                    "file" to mapOf("jarVersion" to project.version),
                    "computercraftVersion" to computercraftVersion,
                ),
            )
        }
        exclude(".cache")
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        if (name == "compileKotlin") {
            source(project(":${forgeShacking.commonProjectName.get()}").sourceSets.main.get().allSource)
        }
    }
    withType<JavaCompile> {
        if (name == "compileJava") {
            source(project(":${forgeShacking.commonProjectName.get()}").sourceSets.main.get().allSource)
        }
    }
}

tasks.jar {
    finalizedBy("reobfJar")
    archiveClassifier.set("")
}
