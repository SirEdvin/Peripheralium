package site.siredvin.peripheralium.util

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

object ContainerHelpers {

    fun moveBetweenInventories(from: Container, fromSlot: Int, to: Container, toSlot: Int, limit: Int): Int {
        // Moving nothing is easy
        if (limit == 0) {
            return 0
        }

        // Get stack to move
        val stack = takeItems(from, limit, fromSlot, fromSlot)
        if (stack.isEmpty) {
            return 0
        }
        val stackCount = stack.count

        // Move items in
        val remainder: ItemStack = if (toSlot < 0) {
            storeItems(stack, to)
        } else {
            storeItems(stack, to, toSlot, toSlot)
        }

        // Calculate items moved
        val count = stackCount - remainder.count
        if (!remainder.isEmpty) {
            // Put the remainder back
            storeItems(remainder, from, fromSlot, fromSlot)
        }
        return count
    }

    fun extract(inventory: Container, slot: Int, limit: Int, previousStack: ItemStack): ItemStack {
        val existingStack = inventory.getItem(slot)
        if (existingStack.isEmpty || (!previousStack.isEmpty && !canStack(previousStack, existingStack)))
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

    fun takeItems(inventory: Container, limit: Int, startSlot: Int, endSlot: Int = -1): ItemStack {
        val realEndSlot = if (endSlot == -1) inventory.containerSize - 1 else endSlot
        var slidingLimit = limit
        var stack = ItemStack.EMPTY
        for (currentSlot in startSlot..realEndSlot) {
            if (limit <= 0)
                return stack
            val extractedStack = extract(inventory, currentSlot, slidingLimit, stack)
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

    fun storeItems(output: ItemStack, inventory: Container, startSlot: Int = 0, endSlot: Int = -1): ItemStack {
        val maxStackSize = minOf(output.maxStackSize, inventory.maxStackSize)
        val realEndSlot = if (endSlot == -1) inventory.containerSize - 1 else endSlot
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

    fun toInventoryOrToWorld(output: ItemStack, inventory: Container, startSlot: Int, outputPos: BlockPos, level: Level) {
        val rest = storeItems(output, inventory, startSlot)
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}