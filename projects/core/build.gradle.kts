@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("peripheralium.vanilla")
    id("peripheralium.publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

baseShaking {
    projectPart.set("common")
    shake()
}

vanillaShaking {
    accessWideners.set(listOf(
        "src/main/resources/peripheralium-common.accesswidener",
        "src/main/resources/peripheralium.accesswidener",
    ))
    shake()
}

sourceSets {
    create("testFixtures") {
        compileClasspath += main.get().compileClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    test {
        compileClasspath += sourceSets["testFixtures"].output
        runtimeClasspath += sourceSets["testFixtures"].output
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    api(libs.bundles.apicommon)

    add(sourceSets["testFixtures"].compileOnlyConfigurationName, kotlin("test"))
    add(sourceSets["testFixtures"].compileOnlyConfigurationName, libs.bundles.test)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.test)
}

java.registerFeature("testFixtures") {
    usingSourceSet(sourceSets.getByName("testFixtures"))
    disablePublication()
}

tasks.test {
    useJUnitPlatform()
}