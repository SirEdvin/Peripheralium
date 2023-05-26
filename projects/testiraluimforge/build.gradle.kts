import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.CurseUploadTask
import com.matthewprenger.cursegradle.Options
import org.jetbrains.changelog.date
import site.siredvin.peripheralium.gradle.mavenDependencies

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
    id("net.minecraftforge.gradle") version "5.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
}

val testVersion: String by extra
val minecraftVersion: String by extra
val testBaseName: String by extra

base {
    archivesName.set("$testBaseName-forge-$minecraftVersion")
    version = testVersion
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

minecraft {
    val extractedLibs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    mappings("parchment", "${extractedLibs.findVersion("parchmentMc").get()}-${extractedLibs.findVersion("parchment").get()}-$minecraftVersion")

//    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        all {
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            forceExit = false
        }

        val client by registering {
            workingDirectory(file("run"))
        }

        val server by registering {
            workingDirectory(file("run/server"))
            arg("--nogui")
        }
    }
}

dependencies {
    val extractedLibs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    minecraft("net.minecraftforge:forge:$minecraftVersion-${extractedLibs.findVersion("forge").get()}")
    implementation(libs.bundles.kotlin)

    compileOnly(project(":testiralium")) {
        exclude("cc.tweaked")
    }
//    implementation(libs.bundles.forge.raw)
    libs.bundles.forge.base.get().map { implementation(fg.deobf(it)) }

//    libs.bundles.externalMods.forge.runtime.get().map { runtimeOnly(fg.deobf(it)) }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        if (name != "compileTestKotlin") {
            source(project(":testiralium").sourceSets.main.get().allSource)
        }
    }
}
//
//tasks.test {
//    useJUnitPlatform()
//}

tasks.jar {
    finalizedBy("reobfJar")
    archiveClassifier.set("")
}

tasks.jarJar {
    finalizedBy("reobfJarJar")
    archiveClassifier.set("jarjar")
}

val rootProjectDir: File by extra

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])
            fg.component(this)

            mavenDependencies {
                exclude(dependencies.create("site.siredvin:"))
                exclude(libs.jei.forge.get())
            }
        }
    }
}
