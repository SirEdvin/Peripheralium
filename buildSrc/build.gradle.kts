// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0
//
// Shameless copy from CC:Tweaked, again

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
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