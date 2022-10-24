package site.siredvin.peripheralium.util

import dan200.computercraft.shared.util.InventoryUtil
import dan200.computercraft.shared.util.ItemStorage
import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

object CCItemStorageHelpers {
    fun moveBetweenInventories(from: ItemStorage, fromSlot: Int, to: ItemStorage, toSlot: Int, limit: Int): Int {
        // Moving nothing is easy
        if (limit == 0) {
            return 0
        }

        // Get stack to move
        val stack = InventoryUtil.takeItems(limit, from, fromSlot, 1, fromSlot)
        if (stack.isEmpty) {
            return 0
        }
        val stackCount = stack.count

        // Move items in
        val remainder: ItemStack = if (toSlot < 0) {
            InventoryUtil.storeItems(stack, to)
        } else {
            InventoryUtil.storeItems(stack, to, toSlot, 1, toSlot)
        }

        // Calculate items moved
        val count = stackCount - remainder.count
        if (!remainder.isEmpty) {
            // Put the remainder back
            InventoryUtil.storeItems(remainder, from, fromSlot, 1, fromSlot)
        }
        return count
    }

    fun toInventoryOrToWorld(output: ItemStack, inventory: Container, startSlot: Int, outputPos: BlockPos, level: Level) {
        val rest =
            InventoryUtil.storeItems(output, ItemStorage.wrap(inventory), startSlot)
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}