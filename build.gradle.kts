plugins {
    id("site.siredvin.root") version "0.5.5"
    id("site.siredvin.base") version "0.5.5"
    id("site.siredvin.linting") version "0.5.5"
    id("site.siredvin.release") version "0.5.5"
    id("site.siredvin.fabric") version "0.5.5"
    id("site.siredvin.publishing") version "0.5.5"
    id("site.siredvin.mod-publishing") version "0.5.5"
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

subprojectShaking {
    withKotlin.set(true)
}

subprojectShaking.setupSubproject(project)

baseShaking {
    projectPart.set("fabric")
    integrationRepositories.set(true)
    shake()
}

fabricShaking {
    commonProjectName.set("")
    createRefmap.set(true)
    extraVersionMappings.set(
        mapOf(
            "computercraft" to "cc-restitched",
        ),
    )
    shake()
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
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    implementation(libs.bundles.java)
    modImplementation(libs.bundles.fabric.core)
    modImplementation(libs.bundles.fabric)
    modImplementation(libs.bundles.ccfabric) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }

    modRuntimeOnly(libs.bundles.externalMods.fabric.runtime) {
        exclude("net.fabricmc.fabric-api")
        exclude("net.fabricmc", "fabric-loader")
    }
}

githubShaking {
    modBranch.set("1.18")
    useFabric.set(false)
    useForge.set(false)
    useRoot.set(true)
    shake()
}

publishingShaking {
    shake()
}

modPublishing {
    output.set(tasks.remapJar)
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "fabric-language-kotlin",
            "peripheralium",
        ),
    )
    requiredDependenciesCurseforge.add("forge-config-api-port-fabric")
    requiredDependenciesModrinth.add("forge-config-api-port")
    shake()
}
