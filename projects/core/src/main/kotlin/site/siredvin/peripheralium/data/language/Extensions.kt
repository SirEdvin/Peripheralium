package site.siredvin.peripheralium.data.language

import net.minecraft.resources.ResourceLocation

fun ResourceLocation.toTurtleTranslationKey(): String = "turtle.${this.toString().replace(":", ".")}"

fun ResourceLocation.toPocketTranslationKey(): String = "pocket.${this.toString().replace(":", ".")}"

fun ResourceLocation.toStatTranslationKey(): String = "stat.${this.namespace}.${this.path}"
