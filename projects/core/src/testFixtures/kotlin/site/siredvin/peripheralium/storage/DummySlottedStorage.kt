package site.siredvin.peripheralium.storage

import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.StorageUtils
import java.util.function.Predicate

class DummySlottedStorage(override val size: Int, initialItems: List<ItemStack>): SlottedStorage {

    val items: Array<ItemStack> = Array(size) { ItemStack.EMPTY }

    init {
        initialItems.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                items[index] = itemStack.copy()
        }
    }

    override fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack {
        var slidingStack = ItemStack.EMPTY
        var slidingLimit = limit
        for (currentSlot in startSlot..endSlot) {
            val stack = items[currentSlot]
                if (!stack.isEmpty && predicate.test(stack)) {
                    if (slidingStack.isEmpty) {
                        slidingStack = stack
                        slidingLimit = minOf(limit, stack.maxStackSize) - stack.count
                        items[currentSlot] = ItemStack.EMPTY
                    } else if (StorageUtils.canMerge(slidingStack, stack)) {
                        val originalCount = stack.count
                        val remainder = StorageUtils.inplaceMerge(slidingStack, stack)
                        slidingLimit -= originalCount - remainder.count
                        if (remainder.isEmpty)
                            items[currentSlot] = ItemStack.EMPTY
                    }
                }
            if (slidingLimit > 0) {
                break
            }
        }
        return slidingStack
    }

    override fun getItem(slot: Int): ItemStack {
        return items[slot]
    }

    override fun canPlaceItem(slot: Int, item: ItemStack): Boolean {
        return true
    }

    override fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack {
        for (currentSlot in startSlot..endSlot) {
            val currentStack = getItem(currentSlot)
            if (currentStack.isEmpty) {
                items[currentSlot] = stack
                return ItemStack.EMPTY
            } else if (StorageUtils.canMerge(currentStack, stack)) {
                val remainder = StorageUtils.inplaceMerge(currentStack, stack)
                if (remainder.isEmpty)
                    return ItemStack.EMPTY
            }

        }
        return stack
    }

    override fun setChanged() {
    }

}