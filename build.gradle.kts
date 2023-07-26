import org.jetbrains.changelog.date
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseUploadTask
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options

plugins {
    id("fabric-loom")
    id("maven-publish")
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("org.jetbrains.changelog") version "1.3.1"
    id("com.modrinth.minotaur") version "2.+"

    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
}

val artifactName: String by project
val modVersion: String by project

base {
    archivesName.set(artifactName)
    version = modVersion
}

val isSnapshotVersion = System.getProperty("snapshot").toBoolean()
val minecraftVersion: String by project
val modVersionWithMC = "$modVersion-$minecraftVersion"
version = if (!isSnapshotVersion) modVersionWithMC else "$modVersionWithMC-SNAPSHOT"
val mavenGroup: String by project
group = mavenGroup

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
val curseforgeKey = secretEnv["CURSEFORGE_KEY"] ?: System.getenv("CURSEFORGE_KEY") ?: ""
val modrinthKey = secretEnv["MODRINTH_KEY"] ?: System.getenv("MODRINTH_KEY") ?: ""

loom {
    runs {
        create("datagen") {
            client()
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.modid=peripheralium")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.strict-validation")
        }
    }
}


sourceSets.main.configure {
    resources.srcDir("src/generated/resources")
}

repositories {
    maven {
        url = uri("https://squiddev.cc/maven")
    }
    maven { url = uri("https://jitpack.io") }
    maven {
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        url = uri("https://maven.terraformersmc.com/")
    }
    maven { url = uri("https://cursemaven.com") }
}

dependencies {
    val loaderVersion: String by project
    val fabricVersion: String by project
    val fabricKotlinVersion: String by project
    val reiVersion: String by project
    val forgeConfigVersion: String by project
    val ccrVersion: String by project

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    modImplementation("com.github.cc-tweaked:cc-restitched:v${minecraftVersion}-${ccrVersion}")
    modImplementation("curse.maven:forgeconfigapirt-fabric-${forgeConfigVersion}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${reiVersion}")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${reiVersion}")
}

tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}

changelog {
    version.set(modVersion)
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf())
}

val CURSEFORGE_RELEASE_TYPE: String by project
val CURSEFORGE_ID: String by project

curseforge {
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
    apiKey = curseforgeKey
    project(closureOf<CurseProject> {
        id = CURSEFORGE_ID
        releaseType = CURSEFORGE_RELEASE_TYPE
        addGameVersion("Fabric")
        addGameVersion(minecraftVersion)
        try {
            changelog = "${project.changelog.get(project.version as String).withHeader(false).toText()}"
            changelogType = "markdown"
        } catch (ignored: Exception) {
            changelog = "Seems not real release"
            changelogType = "markdown"
        }
        mainArtifact(tasks.remapJar.get().archivePath, closureOf<CurseArtifact> {
            displayName = "Peripheralium $version"
            relations(closureOf<CurseRelation> {
                requiredDependency("cc-restitched")
                requiredDependency("forge-config-api-port-fabric")
                requiredDependency("fabric-language-kotlin")
            })
        })
    })
}
project.afterEvaluate {
    tasks.getByName<CurseUploadTask>("curseforge${CURSEFORGE_ID}") {
        dependsOn(tasks.remapJar)
    }
}


val MODRINTH_ID: String by project
val MODRINTH_RELEASE_TYPE: String by project

modrinth {
    token.set(modrinthKey)
    projectId.set(MODRINTH_ID)
    versionNumber.set("${minecraftVersion}-${project.version}")
    versionName.set("Peripheralium ${minecraftVersion} ${version}")
    versionType.set(MODRINTH_RELEASE_TYPE)
    uploadFile.set(tasks.remapJar.get())
    gameVersions.set(listOf(minecraftVersion))
    loaders.set(listOf("fabric")) // Must also be an array - no need to specify this if you're using Loom or ForgeGradl
    try {
        changelog.set("${project.changelog.get(project.version as String).withHeader(false).toText()}")
    } catch (ignored: Exception) {
        changelog.set("")
    }
    dependencies {
        required.project("fabric-language-kotlin")
        required.project("cc-restitched")
        required.project("forge-config-api-port")
    }
}

tasks.create("uploadMod") {
    dependsOn(tasks.modrinth, tasks.curseforge)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
        repositories {
            val modVersion: String by project.extra
            val isUnstable = modVersion.split("-").size > 1
            if (isUnstable) {
                maven("https://mvn.siredvin.site/snapshots") {
                    name = "SirEdvin"
                    credentials(PasswordCredentials::class)
                }
            } else {
                maven("https://mvn.siredvin.site/minecraft") {
                    name = "SirEdvin"
                    credentials(PasswordCredentials::class)
                }
            }
        }
    }
}