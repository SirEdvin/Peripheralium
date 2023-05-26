import site.siredvin.peripheralium.gradle.ConfigureFabric
import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("peripheralium.fabric")
    id("peripheralium.publishing")
    id("peripheralium.mod-publishing")
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra
val configureMinecraft: ConfigureFabric by extra

configureMinecraft.configure(
    modBaseName,
    modVersion,
    minecraftVersion,
    project(":core").file("src/main/resources/peripheralium.accesswidener"),
    "core",
)

repositories {
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
}

sourceSets {
    test {
        compileClasspath += project(":core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":core").sourceSets["testFixtures"].output
    }
}

dependencies {
    implementation(libs.bundles.kotlin)

    // TODO: dark mark here, if I will try to move this dependency
    // to libs it will change down toi 0.14.17
    // Like, what???
    modImplementation("net.fabricmc:fabric-loader:0.14.19")

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

    testImplementation(kotlin("test"))
    testCompileOnly(libs.autoService)
    testAnnotationProcessor(libs.autoService)
    testImplementation(libs.byteBuddy)
    testImplementation(libs.byteBuddyAgent)
    testImplementation(libs.bundles.test)
}

tasks.test {
    dependsOn(tasks.generateDLIConfig)
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
}

modPublishing {
    output.set(tasks.remapJar)
    requiredDependencies.set(
        listOf(
            "cc-tweaked",
            "forge-config-api-port-fabric",
            "fabric-language-kotlin",
        ),
    )
}

publishing {
    publications {
        named<MavenPublication>("maven") {
            mavenDependencies {
                exclude(dependencies.create("site.siredvin:"))
                exclude(libs.jei.fabric.get())
            }
        }
    }
}
