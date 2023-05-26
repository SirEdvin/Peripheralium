package site.siredvin.peripheralium.gradle

fun interface ConfigureProject {
    fun configure(modBaseName: String, modVersion: String, part: String, minecraftVersion: String)
}

fun interface ConfigureVanillaMinecraft{
    fun configure(minecraftVersion: String, vararg accessWideners: String)
}

