package site.siredvin.peripheralium.gradle

import java.io.File

fun interface ConfigureProject {
    fun configure(modBaseName: String, modVersion: String, part: String, minecraftVersion: String)
}

fun interface ConfigureVanillaMinecraft{
    fun configure(minecraftVersion: String, vararg accessWideners: String)
}

fun interface ConfigureFabric {
    fun configure(modBaseName: String, modVersion: String, minecraftVersion: String, accessWidener: File, commonProjectName: String)
}