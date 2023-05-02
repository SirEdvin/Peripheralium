package site.siredvin.peripheralium.api.storage

import dan200.computercraft.api.lua.LuaException
import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object StorageUtils {
    private val ALWAYS_PREDICATE: Predicate<ItemStack> = Predicate { true }

    private fun moveForMovableStorages(from: Storage, to: TargetableStorage, limit: Int, fromSlot: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        if (from is MovableStorage) {
            if (to is MovableStorage && from.movableType != to.movableType)
                throw IllegalArgumentException("To ${to.movableType} and from ${from.movableType} shouldn't compete!")
            return if (fromSlot < 0) {
                from.moveTo(to, limit, toSlot, takePredicate)
            } else {
                if (from !is MovableSlottedStorage)
                    throw LuaException("From storage doesn't support slotting")
                from.moveTo(to, limit, fromSlot, toSlot, takePredicate)
            }
        } else if (to is MovableStorage) {
            return if (toSlot < 0) {
                to.moveFrom(from, limit, fromSlot, takePredicate)
            } else {
                if (to !is MovableSlottedStorage)
                    throw LuaException("To storage doesn't support slotting")
                to.moveTo(from, limit, fromSlot, toSlot, takePredicate)
            }
        }
        return -1
    }

    fun moveBetweenStorages(
        from: Storage, to: TargetableStorage, limit: Int, fromSlot: Int = -1,
        toSlot: Int = -1, takePredicate: Predicate<ItemStack> = ALWAYS_PREDICATE): Int {
        // Moving nothing is easy
        if (limit == 0) {
            return 0
        }
        // Process movable storage logic first
        val movableMoved = moveForMovableStorages(from, to, limit, fromSlot, toSlot, takePredicate)
        if (movableMoved != -1)
            return movableMoved

        // Get stack to move
        val stack = if (fromSlot < 0) {
            from.takeItems(takePredicate, limit)
        } else {
            if (from !is SlottedStorage)
                throw LuaException("From storage doesn't support slotting")
            from.takeItems(limit, fromSlot, fromSlot, takePredicate)
        }
        val stackCount = stack.count

        // Move item to
        val remainder = if (toSlot < 0) {
            to.storeItem(stack)
        } else {
            if (to !is SlottedStorage)
                throw LuaException("To storage doesn't support slotting")
            to.storeItem(stack, toSlot, toSlot)
        }

        // Calculate items moved
        val count = stackCount - remainder.count
        if (!remainder.isEmpty) {
            // Put reminder back
            if (fromSlot < 0) {
                from.storeItem(remainder)
            } else {
                if (from !is SlottedStorage)
                    throw LuaException("From storage doesn't support slotting")
                from.storeItem(remainder, fromSlot, fromSlot)
            }
        }
        return count
    }

    fun canStack(first: ItemStack, second: ItemStack): Boolean {
        if (!first.sameItem(second))
            return false
        return first.damageValue == second.damageValue && ItemStack.tagMatches(first, second)
    }

    fun canMerge(first: ItemStack, second: ItemStack, stackLimit: Int = -1): Boolean {
        if (!canStack(first, second))
            return false
        val realStackLimit = if (stackLimit == -1) first.maxStackSize else minOf(stackLimit, first.maxStackSize)
        return first.count < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: ItemStack, second: ItemStack, mergeLimit: Int = Int.MAX_VALUE): ItemStack {
        if (!canMerge(first, second))
            return second
        val mergeSize = minOf(second.count, first.maxStackSize - first.count, mergeLimit)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty)
            return ItemStack.EMPTY
        return second
    }

    fun toInventoryOrToWorld(output: ItemStack, inventory: SlottedStorage, startSlot: Int, outputPos: BlockPos, level: Level) {
        val rest = inventory.storeItem(output, startSlot)
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}