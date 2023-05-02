package site.siredvin.peripheralium.api.storage

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

class TargetableContainer(private val container: Container): SlottedStorage {

    fun extract(slot: Int, limit: Int, previousStack: ItemStack): ItemStack {
        return extract(slot, limit) { previousStack.isEmpty || StorageUtils.canStack(previousStack, it) }
    }

    fun extract(slot: Int, limit: Int, predicate: Predicate<ItemStack>): ItemStack {
        val existingStack = container.getItem(slot)
        if (existingStack.isEmpty || !predicate.test(existingStack))
            return ItemStack.EMPTY
        if (existingStack.count < limit) {
            container.setItem(slot, ItemStack.EMPTY)
            container.setChanged()
            return existingStack
        }
        val resultedStack = existingStack.split(limit)
        container.setItem(slot, existingStack)
        container.setChanged()
        return resultedStack
    }

    override fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack {
        val realEndSlot = if (endSlot == -1) container.containerSize - 1 else endSlot
        var slidingLimit = limit
        var stack = ItemStack.EMPTY
        for (currentSlot in startSlot..realEndSlot) {
            if (limit <= 0)
                return stack
            val extractedStack = if (stack.isEmpty)
                extract(currentSlot, slidingLimit, stack)
            else
                extract(currentSlot, slidingLimit, predicate)
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

    override fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack {
        val maxStackSize = minOf(stack.maxStackSize, container.containerSize)
        val realEndSlot = if (endSlot == -1) container.containerSize - 1 else endSlot
        if (maxStackSize <= 0)
            return stack

        var slidingStack = stack
        for (currentSlot in startSlot..realEndSlot) {
            val slotStack = container.getItem(currentSlot)
            if (slotStack.isEmpty) {
                if (!container.canPlaceItem(currentSlot, slidingStack))
                    continue
                if (slidingStack.count <= maxStackSize) {
                    container.setItem(currentSlot, slidingStack)
                    return ItemStack.EMPTY
                } else {
                    container.setItem(currentSlot, slidingStack.split(maxStackSize))
                }
            } else {
                val slotMaxStackSize = minOf(slotStack.maxStackSize, maxStackSize)
                if (slotStack.count >= slotMaxStackSize)
                    continue
                if (!StorageUtils.canMerge(slotStack, slidingStack, slotMaxStackSize))
                    continue

                slidingStack = StorageUtils.inplaceMerge(slotStack, slidingStack)

                if (slidingStack.isEmpty)
                    return ItemStack.EMPTY
            }
        }
        return slidingStack
    }

    override fun getItem(slot: Int): ItemStack {
        return container.getItem(slot)
    }

    override fun canPlaceItem(slot: Int, item: ItemStack): Boolean {
        return container.canPlaceItem(slot, item)
    }

    override fun setChanged() {
        container.setChanged()
    }

    override val size: Int
        get() = container.containerSize

}