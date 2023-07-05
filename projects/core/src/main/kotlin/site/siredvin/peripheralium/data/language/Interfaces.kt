package site.siredvin.peripheralium.data.language

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

interface ModInformationHolder {

    val items: List<Supplier<out Item>>
    val blocks: List<Supplier<out Block>>
    val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
    val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
}

interface TextRecord {
    val textID: String
    val text: MutableComponent
        get() = Component.translatable(textID)
    fun format(vararg args: Any): MutableComponent {
        return Component.translatable(textID, *args)
    }
}
