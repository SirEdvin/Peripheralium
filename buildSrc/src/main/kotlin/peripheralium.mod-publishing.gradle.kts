import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.jetbrains.changelog.date
import site.siredvin.peripheralium.gradle.setProvider

plugins {
    id("net.darkhax.curseforgegradle")
    id("com.modrinth.minotaur")
    id("org.jetbrains.changelog")
}

abstract class ModPublishingExtension {
    abstract val output: Property<AbstractArchiveTask>
    abstract val requiredDependencies: ListProperty<String>
    init {
        output.finalizeValueOnRead()
    }
}

val modPublishing = project.extensions.create("modPublishing", ModPublishingExtension::class.java)
val rootProjectDir: File by extra
val minecraftVersion: String by extra
val modVersion: String by extra
val CURSEFORGE_RELEASE_TYPE: String by extra
val CURSEFORGE_ID: String by extra
val curseforgeKey: String by extra

changelog {
    version.set(modVersion)
    path.set("$rootProjectDir/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf())
}

val publishCurseForge by tasks.registering(TaskPublishCurseForge::class) {
    group = PublishingPlugin.PUBLISH_TASK_GROUP
    description = "Upload artifacts to CurseForge"
    apiToken = curseforgeKey
    enabled = apiToken != ""

    val mainFile = upload(CURSEFORGE_ID, modPublishing.output.get().archiveFile)
    // Power of SquidDev is truly terrifuning
    dependsOn(modPublishing.output) // See https://github.com/Darkhax/CurseForgeGradle/pull/7.
    mainFile.releaseType = CURSEFORGE_RELEASE_TYPE
    mainFile.gameVersions.add(minecraftVersion)
    mainFile.changelog = changelog.renderItem(project.changelog.get(modVersion).withHeader(false))
    mainFile.changelogType = "markdown"
    modPublishing.requiredDependencies.get().forEach(mainFile::addRequirement)
}

val MODRINTH_ID: String by extra
val MODRINTH_RELEASE_TYPE: String by extra
val modrinthKey: String by extra

modrinth {
    token.set(modrinthKey)
    projectId.set(MODRINTH_ID)
    versionNumber.set("$minecraftVersion-${project.version}")
    versionName.set("$minecraftVersion-${project.version}")
    versionType.set(MODRINTH_RELEASE_TYPE)
    uploadFile.setProvider(modPublishing.output)
    gameVersions.add(minecraftVersion)
    changelog.set(project.changelog.renderItem(project.changelog.get(modVersion).withHeader(false)))
    dependencies {
        modPublishing.requiredDependencies.get().forEach(required::project)
    }
}