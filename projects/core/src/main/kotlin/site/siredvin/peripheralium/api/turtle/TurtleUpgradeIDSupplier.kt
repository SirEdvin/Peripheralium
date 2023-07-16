package site.siredvin.peripheralium.api.turtle

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import site.siredvin.peripheralium.xplat.XplatRegistries

fun interface TurtleUpgradeIDSupplier {
    companion object {
        val IDENTIC = TurtleUpgradeIDSupplier { XplatRegistries.ITEMS.getKey(it) }
        val WITHOUT_CORE = TurtleUpgradeIDSupplier {
            val base = IDENTIC.get(it)
            // To cutoff _core part
            return@TurtleUpgradeIDSupplier ResourceLocation(base.namespace, base.path.replace("_core", ""))
        }
        val WITHOUT_TURTLE = TurtleUpgradeIDSupplier {
            val base = IDENTIC.get(it)
            // To cutoff _core part
            return@TurtleUpgradeIDSupplier ResourceLocation(base.namespace, base.path.replace("turtle_", ""))
        }
    }

    fun get(item: Item): ResourceLocation
}
