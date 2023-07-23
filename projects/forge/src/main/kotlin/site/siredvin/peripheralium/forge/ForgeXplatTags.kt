package site.siredvin.peripheralium.forge

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.IForgeShearable
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

    override fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean> {
        if (entity is IForgeShearable) {
            return Pair(true, entity.isShearable(targetItem, entity.level(), entity.blockPosition()))
        }
        return Pair(false, false)
    }
}
