import org.gradle.kotlin.dsl.repositories
import site.siredvin.peripheralium.gradle.ConfigureProject

plugins {
    java
}

repositories {
    mavenCentral()
    // For CC:T common code
    maven {
        url = uri("https://squiddev.cc/maven/")
        content {
            includeGroup("cc.tweaked")
            includeModule("org.squiddev", "Cobalt")
        }
    }
    // For Forge configuration common code
    maven {
        name = "Fuzs Mod Resources"
        url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        content {
            includeGroup("fuzs.forgeconfigapiport")
        }
    }
}

extra["connectMainIntegrations"] = Runnable {
    repositories {
        maven {
            url = uri("https://www.cursemaven.com")
            name = "Curse Maven"
            content {
                includeGroup("curse.maven")
            }
        }
        maven {
            url = uri("https://api.modrinth.com/maven")
            name = "Modrinth"
            content {
                includeGroup("maven.modrinth")
            }
        }
        maven {
            url = uri("https://maven.architectury.dev/")
            content {
                includeGroup("dev.architectury")
            }
        }
    }
}

extra["configureProject"] = ConfigureProject { modBaseName, modVersion, projectPart, minecraftVersion ->

    base {
        archivesName.set("$modBaseName-$projectPart-$minecraftVersion")
        version = modVersion
    }

    sourceSets.main.configure {
        resources.srcDir("src/generated/resources")
    }
}