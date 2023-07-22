package site.siredvin.peripheralium.forge

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.Tags
import site.siredvin.peripheralium.xplat.XplatTags

object ForgeXplatTags : XplatTags {
    override fun isOre(state: BlockState): Boolean {
        return state.`is`(Tags.Blocks.ORES)
    }

    override fun isOre(stack: ItemStack): Boolean {
        return stack.`is`(Tags.Items.ORES)
    }

    override fun isBookshelf(state: BlockState): Boolean {
        return state.`is`(Tags.Blocks.BOOKSHELVES)
    }

    override fun isBookshelf(stack: ItemStack): Boolean {
        return stack.`is`(Tags.Items.BOOKSHELVES)
    }
}
