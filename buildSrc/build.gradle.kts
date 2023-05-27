import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("groovy-gradle-plugin")
}

// Duplicated in settings.gradle.kts
repositories {
    mavenCentral()
    gradlePluginPortal()

    maven("https://mvn.siredvin.site/minecraft") {
        name = "SirEdvin's Minecraft repository"
        content {
            includeGroup("net.minecraftforge")
            includeGroup("net.minecraftforge.gradle")
            includeGroup("org.parchmentmc")
            includeGroup("org.parchmentmc.feather")
            includeGroup("org.parchmentmc.data")
            includeGroup("org.spongepowered")
            includeGroup("net.fabricmc")
        }
    }
}

dependencies {
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.spotless)
    implementation(libs.plugin.vanillaGradle)
    implementation(libs.plugin.loom)
    implementation(libs.plugin.curseForgeGradle)
    implementation(libs.plugin.minotaur)
    implementation(libs.plugin.changelog)
    implementation(libs.plugin.forgeGradle)
    implementation(libs.plugin.librarian)
}



group = "site.siredvin"
archivesName.set("buildenv")
version = "0.1.0"

gradlePlugin {
    website.set("https://github.com/SirEdvin")
    vcsUrl.set("https://github.com/SirEdvin/Peripheralium")
}

publishing {
    repositories {
        maven("https://mvn.siredvin.site/minecraft") {
            name = "SirEdvin"
            credentials(PasswordCredentials::class)
        }
    }
}



