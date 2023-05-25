package site.siredvin.peripheralium.common.items

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.util.itemTooltip

open class DescriptiveBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    private var _description: MutableComponent? = null

    private val extraDescription: MutableComponent
        get() {
            if (_description == null) {
                _description = itemTooltip(this.descriptionId)
            }
            return _description!!
        }

    override fun appendHoverText(
        itemStack: ItemStack,
        level: Level?,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(itemStack, level, list, tooltipFlag)
        val keyContents = extraDescription.contents as TranslatableContents
        if (keyContents.key != extraDescription.string) {
            list.add(extraDescription)
        }
    }
}
