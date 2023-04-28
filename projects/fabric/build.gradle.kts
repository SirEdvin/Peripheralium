plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.kotlin)
}

val modVersion: String by extra
val minecraftVersion: String by extra
val modBaseName: String by extra

base {
    archivesName.set("${modBaseName}-fabric-${minecraftVersion}")
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
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())

    modImplementation(libs.bundles.fabric)
    modImplementation(libs.bundles.ccfabric)
    modImplementation(project(":core"))
}

loom {
    runs {
        register("FabricClient") {
            client()
        }
        register("FabricServer") {
            server()
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

tasks {
    processResources {
        from(project(":core").sourceSets.main.get().resources)
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    withType<JavaCompile> {
        source(project(":core").sourceSets.main.get().allSource)
    }
}
