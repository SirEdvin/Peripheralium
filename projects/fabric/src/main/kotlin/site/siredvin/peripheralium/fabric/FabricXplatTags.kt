package site.siredvin.peripheralium.fabric

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Shearable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.xplat.XplatTags

object FabricXplatTags : XplatTags {
    override fun isOre(state: BlockState): Boolean {
        return state.`is`(ConventionalBlockTags.ORES)
    }

    override fun isOre(stack: ItemStack): Boolean {
        return stack.`is`(ConventionalItemTags.ORES)
    }

    override fun isBookshelf(state: BlockState): Boolean {
        return state.`is`(ConventionalBlockTags.BOOKSHELVES)
    }

    override fun isBookshelf(stack: ItemStack): Boolean {
        return stack.`is`(ConventionalItemTags.BOOKSHELVES)
    }

    override fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean> {
        if (entity !is Shearable) {
            return Pair(false, false)
        }
        return Pair(true, entity.readyForShearing())
    }
}
