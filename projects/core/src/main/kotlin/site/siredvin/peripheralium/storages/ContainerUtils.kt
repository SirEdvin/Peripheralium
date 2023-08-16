package site.siredvin.peripheralium.storages

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.storages.item.ItemStorageUtils
import java.util.function.Predicate

object ContainerUtils {
    fun extract(container: Container, slot: Int, limit: Int, previousStack: ItemStack): ItemStack {
        return extract(container, slot, limit) { previousStack.isEmpty || ItemStorageUtils.canStack(previousStack, it) }
    }

    fun extract(container: Container, slot: Int, limit: Int, predicate: Predicate<ItemStack>): ItemStack {
        val existingStack = container.getItem(slot)
        if (existingStack.isEmpty || !predicate.test(existingStack)) {
            return ItemStack.EMPTY
        }
        return container.removeItem(slot, limit)
    }

    fun takeItems(container: Container, limit: Int, startSlot: Int = 0, endSlot: Int = -1, predicate: Predicate<ItemStack>): ItemStack {
        var slidingPredicate = predicate
        val realEndSlot = if (endSlot == -1) container.containerSize - 1 else endSlot
        var slidingLimit = limit
        var stack = ItemStack.EMPTY
        for (currentSlot in startSlot..realEndSlot) {
            if (limit <= 0) {
                return stack
            }
            val extractedStack = extract(container, currentSlot, slidingLimit, predicate)
            if (extractedStack.isEmpty) {
                continue
            }
            slidingLimit -= extractedStack.count
            if (stack.isEmpty) { // first time something successfully extracted
                stack = extractedStack
                slidingLimit = minOf(slidingLimit, stack.maxStackSize)
                slidingPredicate = slidingPredicate.and {
                    ItemStorageUtils.canStack(stack, it)
                }
            } else {
                stack.grow(extractedStack.count)
            }
        }
        container.setChanged()
        return stack
    }

    fun storeItem(container: Container, stack: ItemStack, startSlot: Int = 0, endSlot: Int = -1): ItemStack {
        val maxStackSize = minOf(stack.maxStackSize, container.maxStackSize)
        val realEndSlot = if (endSlot == -1) container.containerSize - 1 else endSlot
        if (maxStackSize <= 0) {
            return stack
        }

        var slidingStack = stack
        for (currentSlot in startSlot..realEndSlot) {
            val slotStack = container.getItem(currentSlot)
            if (slotStack.isEmpty) {
                if (!container.canPlaceItem(currentSlot, slidingStack)) {
                    continue
                }
                if (slidingStack.count <= maxStackSize) {
                    container.setItem(currentSlot, slidingStack)
                    return ItemStack.EMPTY
                } else {
                    container.setItem(currentSlot, slidingStack.split(maxStackSize))
                }
            } else {
                val slotMaxStackSize = minOf(slotStack.maxStackSize, maxStackSize)
                if (slotStack.count >= slotMaxStackSize) {
                    continue
                }
                if (!ItemStorageUtils.canMerge(slotStack, slidingStack, slotMaxStackSize)) {
                    continue
                }

                slidingStack = ItemStorageUtils.inplaceMerge(slotStack, slidingStack)

                if (slidingStack.isEmpty) {
                    container.setChanged()
                    return ItemStack.EMPTY
                }
            }
        }
        container.setChanged()
        return slidingStack
    }

    fun toInventoryOrToWorld(output: ItemStack, container: Container, startSlot: Int, outputPos: BlockPos, level: Level) {
        var rest = storeItem(container, output, startSlot, container.containerSize - 1)
        if (!rest.isEmpty && startSlot > 0) {
            rest = storeItem(container, output, 0, startSlot - 1)
        }
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}
