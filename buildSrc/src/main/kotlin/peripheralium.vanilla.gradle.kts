plugins {
    java
    id("org.spongepowered.gradle.vanilla")
}

class VanillaShakingExtension(private val targetProject: Project) {
    val accessWideners: ListProperty<String> = targetProject.objects.listProperty(String::class.java)

    fun shake() {
        val minecraftVersion: String by targetProject.extra

        targetProject.minecraft {
            version(minecraftVersion)
            accessWideners(*accessWideners.get().toTypedArray())
        }
    }
}

val vanillaShaking = VanillaShakingExtension(project)
project.extensions.add("vanillaShaking", vanillaShaking)
