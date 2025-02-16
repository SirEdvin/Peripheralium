package site.siredvin.peripheralium.xplat

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

interface XplatTags {
    companion object {
        private var impl: XplatTags? = null

        fun configure(impl: XplatTags) {
            this.impl = impl
        }

        fun get(): XplatTags {
            if (impl == null) {
                throw IllegalStateException("You should init Peripheral Platform first")
            }
            return impl!!
        }

        fun isOre(state: BlockState): Boolean = get().isOre(state)

        fun isOre(stack: ItemStack): Boolean = get().isOre(stack)

        fun isBookshelf(state: BlockState): Boolean = get().isBookshelf(state)

        fun isBookshelf(stack: ItemStack): Boolean = get().isBookshelf(stack)

        fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean> = get().isShearable(entity, targetItem)
    }
    fun isOre(state: BlockState): Boolean

    fun isOre(stack: ItemStack): Boolean

    fun isBookshelf(state: BlockState): Boolean

    fun isBookshelf(stack: ItemStack): Boolean

    /**
     * Check if entity is shearable and is sherable now
     *
     * @return Pair of "is shearable" and "is shearable now" flags
     */
    fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean>
}
