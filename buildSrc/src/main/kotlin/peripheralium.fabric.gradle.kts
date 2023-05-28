plugins {
    java
    id("fabric-loom")
}

fun configureFabric(targetProject: Project, accessWidener: File?, commonProjectName: String, createRefmap: Boolean, versionMappings: Map<String, String>) {
    val minecraftVersion: String by targetProject.extra
    val modBaseName: String by targetProject.extra

    targetProject.dependencies {
        minecraft("com.mojang:minecraft:$minecraftVersion")
        mappings(loom.officialMojangMappings())
        implementation(project(":$commonProjectName")) {
            exclude("cc.tweaked")
        }
    }

    targetProject.loom {
        if (accessWidener != null)
            accessWidenerPath.set(accessWidener)
        if (createRefmap)
            mixin.defaultRefmapName.set("$modBaseName.refmap.json")
        runs {
            named("client") {
                configName = "Fabric Client"
            }
            named("server") {
                configName = "Fabric Server"
            }
            create("data") {
                client()
                vmArg("-Dfabric-api.datagen")
                vmArg("-Dfabric-api.datagen.modid=$modBaseName")
                vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
                vmArg("-Dfabric-api.datagen.strict-validation")
            }
        }
    }

    targetProject.tasks {
        val extractedLibs = targetProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        processResources {
            from(project(":$commonProjectName").sourceSets.main.get().resources)
            inputs.property("version", targetProject.version)
            val basePropertyMap = mutableMapOf(
                "version" to targetProject.version
            )
            versionMappings.entries.forEach {
                inputs.property("${it.key}Version", extractedLibs.findVersion(it.value).get())
                basePropertyMap["${it.key}Version"] = extractedLibs.findVersion(it.value).get()
            }

            filesMatching("fabric.mod.json") {
                expand(basePropertyMap)
            }
            exclude(".cache")
        }
        withType<JavaCompile> {
            if (this.name == "compileJava") {
                source(project(":$commonProjectName").sourceSets.main.get().allSource)
            }
        }
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            if (this.name == "compileKotlin") {
                source(project(":$commonProjectName").sourceSets.main.get().allSource)
            }
        }
    }
}

class FabricShakingExtension(private val targetProject: Project) {
    val commonProjectName: Property<String> = targetProject.objects.property(String::class.java)
    val accessWidener: Property<File> = targetProject.objects.property(File::class.java)
    val createRefmap: Property<Boolean> = targetProject.objects.property(Boolean::class.java)
    val extraVersionMappings: MapProperty<String, String> = targetProject.objects.mapProperty(String::class.java, String::class.java)

    fun shake() {
        createRefmap.convention(false)
        extraVersionMappings.convention(emptyMap())
        configureFabric(targetProject, accessWidener.orNull, commonProjectName.get(), createRefmap.get(), extraVersionMappings.get())
    }
}

val fabricShaking = FabricShakingExtension(project)
project.extensions.add("fabricShaking", fabricShaking)
