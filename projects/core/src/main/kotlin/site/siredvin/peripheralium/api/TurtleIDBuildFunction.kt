package site.siredvin.peripheralium.api

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

fun interface TurtleIDBuildFunction {
    companion object {
        val IDENTIC = TurtleIDBuildFunction { PeripheraliumPlatform.getKey(it) }
        val WITHOUT_CORE = TurtleIDBuildFunction {
            val base = IDENTIC.get(it)
            // To cutoff _core part
            return@TurtleIDBuildFunction ResourceLocation(base.namespace, base.path.replace("_core", ""))
        }
        val WITHOUT_TURTLE = TurtleIDBuildFunction {
            val base = IDENTIC.get(it)
            // To cutoff _core part
            return@TurtleIDBuildFunction ResourceLocation(base.namespace, base.path.replace("turtle_", ""))
        }
    }

    fun get(item: Item): ResourceLocation
}