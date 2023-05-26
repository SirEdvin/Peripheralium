@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.vanillaGradle)
    alias(libs.plugins.kotlin)
    idea
}

val testVersion: String by extra
val minecraftVersion: String by extra
val testBaseName: String by extra

base {
    archivesName.set("$testBaseName-common-$minecraftVersion")
    version = testVersion
}

minecraft {
    version(minecraftVersion)
    accessWideners(
        "src/main/resources/testiralium.accesswidener"
    )
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
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.cccommon)
    implementation(libs.bundles.testerium.common)
    compileOnly(libs.mixin)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}
