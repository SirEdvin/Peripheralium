package site.siredvin.peripheralium.util

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

object ContainerHelpers {

    fun moveBetweenInventories(from: Container, fromSlot: Int, to: Container, toSlot: Int, limit: Int): Int {
        TODO()
    }

    fun canMerge(first: ItemStack, second: ItemStack, stackLimit: Int = -1): Boolean {
        if (!first.sameItem(second))
            return false
        if (first.damageValue != second.damageValue)
            return false
        val realStackLimit = if (stackLimit == -1) first.maxStackSize else minOf(stackLimit, first.maxStackSize)
        if (first.count >= realStackLimit)
            return false
        return ItemStack.tagMatches(first, second)
    }
    fun storeItems(output: ItemStack, inventory: Container, startSlot: Int = 0): ItemStack {
        val maxStackSize = minOf(output.maxStackSize, inventory.maxStackSize)
        if (maxStackSize <= 0)
            return output

        for (currentSlot in startSlot..inventory.containerSize) {
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