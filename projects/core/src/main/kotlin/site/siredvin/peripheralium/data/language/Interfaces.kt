package site.siredvin.peripheralium.data.language

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

interface ModInformationHolder {
    val items: List<Supplier<out Item>>
        get() = emptyList()
    val blocks: List<Supplier<out Block>>
        get() = emptyList()
    val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = emptyList()
    val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = emptyList()
    val customStats: List<Supplier<Stat<ResourceLocation>>>
        get() = emptyList()
}

interface TextRecord {
    val textID: String
    val text: MutableComponent
        get() = Component.translatable(textID)
    fun format(vararg args: Any): MutableComponent {
        return Component.translatable(textID, *args)
    }
}
