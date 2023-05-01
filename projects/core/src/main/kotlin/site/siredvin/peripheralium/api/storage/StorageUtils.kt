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

    fun extract(inventory: SlottedStorage, slot: Int, limit: Int, previousStack: ItemStack): ItemStack {
        return extract(inventory, slot, limit) { previousStack.isEmpty || canStack(previousStack, it) }
    }

    fun extract(inventory: SlottedStorage, slot: Int, limit: Int, predicate: Predicate<ItemStack>): ItemStack {
        val existingStack = inventory.getItem(slot)
        if (existingStack.isEmpty || !predicate.test(existingStack))
            return ItemStack.EMPTY
        if (existingStack.count < limit) {
            inventory.setItem(slot, ItemStack.EMPTY)
            inventory.setChanged()
            return existingStack
        }
        val resultedStack = existingStack.split(limit)
        inventory.setItem(slot, existingStack)
        inventory.setChanged()
        return resultedStack
    }

    fun takeItems(inventory: SlottedStorage, limit: Int, startSlot: Int, endSlot: Int = -1, initialPredicate: Predicate<ItemStack> = ALWAYS_PREDICATE): ItemStack {
        val realEndSlot = if (endSlot == -1) inventory.size - 1 else endSlot
        var slidingLimit = limit
        var stack = ItemStack.EMPTY
        for (currentSlot in startSlot..realEndSlot) {
            if (limit <= 0)
                return stack
            val extractedStack = if (stack.isEmpty)
                extract(inventory, currentSlot, slidingLimit, stack)
            else
                extract(inventory, currentSlot, slidingLimit, initialPredicate)
            if (extractedStack.isEmpty)
                continue
            slidingLimit -= extractedStack.count
            if (stack.isEmpty) { // first time something successfully extracted
                stack = extractedStack
                slidingLimit = minOf(slidingLimit, stack.maxStackSize)
            } else {
                stack.grow(extractedStack.count)
            }
        }
        return stack
    }

    fun canStack(first: ItemStack, second: ItemStack): Boolean {
        if (!first.sameItem(second))
            return false
        return first.damageValue == second.damageValue
    }

    fun canMerge(first: ItemStack, second: ItemStack, stackLimit: Int = -1): Boolean {
        if (!canStack(first, second))
            return false
        val realStackLimit = if (stackLimit == -1) first.maxStackSize else minOf(stackLimit, first.maxStackSize)
        if (first.count >= realStackLimit)
            return false
        return ItemStack.tagMatches(first, second)
    }

    fun storeItems(output: ItemStack, inventory: SlottedStorage, startSlot: Int = 0, endSlot: Int = -1): ItemStack {
        val maxStackSize = minOf(output.maxStackSize, inventory.size)
        val realEndSlot = if (endSlot == -1) inventory.size - 1 else endSlot
        if (maxStackSize <= 0)
            return output

        for (currentSlot in startSlot..realEndSlot) {
            val slotStack = inventory.getItem(currentSlot)
            if (slotStack.isEmpty) {
                if (!inventory.canPlaceItem(currentSlot, output))
                    continue
                if (output.count <= maxStackSize) {
                    inventory.setItem(currentSlot, output)
                    return ItemStack.EMPTY
                } else {
                    inventory.setItem(currentSlot, output.split(maxStackSize))
                }
            } else {
                val slotMaxStackSize = minOf(slotStack.maxStackSize, maxStackSize)
                if (slotStack.count >= slotMaxStackSize)
                    continue
                if (!canMerge(slotStack, output, slotMaxStackSize))
                    continue

                val mergeSize = minOf(output.count, slotMaxStackSize - slotStack.count)
                slotStack.grow(mergeSize)
                output.shrink(mergeSize)
                if (output.isEmpty)
                    return ItemStack.EMPTY
            }
        }
        return output
    }

    fun toInventoryOrToWorld(output: ItemStack, inventory: SlottedStorage, startSlot: Int, outputPos: BlockPos, level: Level) {
        val rest = storeItems(output, inventory, startSlot)
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}