plugins {
    alias(libs.plugins.vanillaGradle)
    alias(libs.plugins.kotlin)
}

val modVersion: String by extra
val minecraftVersion: String by extra

minecraft {
    version(minecraftVersion)
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

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    api(libs.bundles.apicommon)
//    minecraft("com.mojang:minecraft:$minecraftVersion")
}