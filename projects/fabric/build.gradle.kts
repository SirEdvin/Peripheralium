import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.kotlin)
    idea
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

base {
    archivesName.set("${modBaseName}-fabric-${minecraftVersion}")
    version = modVersion
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
    // location of the maven that hosts JEI files since January 2023
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
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

sourceSets {
    test {
        compileClasspath += project(":core").sourceSets["testFixtures"].output
        runtimeClasspath += project(":core").sourceSets["testFixtures"].output
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())

    implementation(libs.bundles.kotlin)

    implementation(project(":core")) {
        exclude("cc.tweaked")
    }

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

loom {
    runs {
        named("client") {
            configName = "Fabric Client"
        }
        named("server") {
            configName = "Fabric Server"
        }
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

tasks.test {
    dependsOn(tasks.generateDLIConfig)
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
}

tasks {
    processResources {
        from(project(":core").sourceSets.main.get().resources)
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
        exclude(".cache")
    }
    withType<JavaCompile> {
        if (this.name != "compileTestJava") {
            source(project(":core").sourceSets.main.get().allSource)
        }
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        if (this.name != "compileTestKotlin") {
            source(project(":core").sourceSets.main.get().allSource)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])

            mavenDependencies {
                exclude(dependencies.create("site.siredvin:"))
                exclude(libs.jei.fabric.get())

            }
        }
    }
    repositories {
        mavenLocal()
    }
}