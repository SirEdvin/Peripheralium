package site.siredvin.peripheralium.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.data.language.ModInformationHolder
import java.util.function.Supplier

class ModInformationTracker : ModInformationHolder {
    val ITEMS: MutableList<Supplier<out Item>> = mutableListOf()
    val BLOCKS: MutableList<Supplier<out Block>> = mutableListOf()
    val POCKET_UPGRADES: MutableList<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>> = mutableListOf()
    val TURTLE_UPGRADES: MutableList<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>> = mutableListOf()
    val CUSTOM_STATS: MutableList<Supplier<Stat<ResourceLocation>>> = mutableListOf()

    override val items: List<Supplier<out Item>>
        get() = ITEMS
    override val blocks: List<Supplier<out Block>>
        get() = BLOCKS
    override val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = POCKET_UPGRADES
    override val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = TURTLE_UPGRADES
    override val customStats: List<Supplier<Stat<ResourceLocation>>>
        get() = CUSTOM_STATS
}
