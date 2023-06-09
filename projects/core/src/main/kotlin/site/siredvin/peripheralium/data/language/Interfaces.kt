package site.siredvin.peripheralium.data.language

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

interface ModInformationHolder {
    fun getItems(): List<Supplier<out Item>>
    fun getBlocks(): List<Supplier<out Block>>
}

interface TextRecord {
    val textID: String
    fun text(vararg args: Any): Component {
        return Component.translatable(textID, *args)
    }
}
