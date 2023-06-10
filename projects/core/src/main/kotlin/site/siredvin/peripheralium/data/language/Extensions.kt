package site.siredvin.peripheralium.data.language

import net.minecraft.resources.ResourceLocation

fun ResourceLocation.toTurtleTranslationKey(): String {
    return "turtle.${this.toString().replace(":", ".")}"
}

fun ResourceLocation.toPocketTranslationKey(): String {
    return "pocket.${this.toString().replace(":", ".")}"
}
