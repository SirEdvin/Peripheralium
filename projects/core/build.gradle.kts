import org.gradle.kotlin.dsl.get

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("site.siredvin.vanilla")
    id("site.siredvin.publishing")
}

baseShaking {
    projectPart.set("common")
    integrationRepositories.set(true)
    shake()
}

vanillaShaking {
    accessWideners.set(
        listOf(
            "src/main/resources/peripheralium-common.accesswidener",
            "src/main/resources/peripheralium.accesswidener",
        ),
    )
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
    libs.bundles.cccommon.get().map {
        implementation(variantOf(provider { it }) { classifier("dev") }) {
            exclude("net.fabricmc.fabric-api")
            exclude("net.fabricmc")
            exclude("com.terraformersmc")
        }
    }
    implementation(libs.bundles.onlycore)
    api(libs.bundles.apicommon)
    compileOnly(libs.mixin)

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

publishingShaking {
    shake()
}
