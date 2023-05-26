import site.siredvin.peripheralium.gradle.ConfigureVanillaMinecraft

plugins {
    java
    id("org.spongepowered.gradle.vanilla")
}

extra["configureMinecraft"] = ConfigureVanillaMinecraft { minecraftVersion, accessWideners ->
    minecraft {
        version(minecraftVersion)
        accessWideners(*accessWideners)
    }
}