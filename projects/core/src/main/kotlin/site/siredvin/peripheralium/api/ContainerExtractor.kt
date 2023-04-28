package site.siredvin.peripheralium.api

import net.minecraft.world.Container
import net.minecraft.world.level.Level

fun interface ContainerExtractor {
    fun extract(level: Level, obj: Any?): Container?
}