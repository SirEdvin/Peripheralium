package site.siredvin.peripheralium.storage

import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.StorageUtils
import java.util.function.Predicate

class DummyStorage(private val maxSlots: Int, initialItems: List<ItemStack>): TestableStorage {

    val items: MutableList<ItemStack> = mutableListOf()

    init {
        if (initialItems.size > maxSlots)
            throw IllegalArgumentException("Max slots is too low for you?")
        initialItems.forEach {
            items.add(it)
        }
        clean()
    }

    override fun getItem(slot: Int): ItemStack {
        return items[slot]
    }

    override fun getItems(): Iterator<ItemStack> {
        return items.iterator()
    }

    fun clean() {
        items.removeIf { it.isEmpty }
    }

    override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack {
        var slidingStack = ItemStack.EMPTY
        var slidingLimit = limit
        val toRemove = mutableListOf<Int>()
        items.forEachIndexed { index, stack ->
            if (slidingLimit > 0) {
                if (!stack.isEmpty && predicate.test(stack)) {
                    if (slidingStack.isEmpty) {
                        slidingStack = stack
                        slidingLimit = minOf(limit, stack.maxStackSize) - stack.count
                        toRemove.add(index)
                    } else if (StorageUtils.canMerge(slidingStack, stack)) {
                        val originalCount = stack.count
                        val remainder = StorageUtils.inplaceMerge(slidingStack, stack)
                        slidingLimit -= originalCount - remainder.count
                        if (remainder.isEmpty)
                            toRemove.add(index)
                    }
                }
            }
        }
        toRemove.asReversed().forEach {
            items.removeAt(it)
        }
        clean()
        return slidingStack
    }

    override fun storeItem(stack: ItemStack): ItemStack {
        items.forEach {
            if (StorageUtils.canMerge(it, stack))
                StorageUtils.inplaceMerge(it, stack)
        }
        if (stack.isEmpty)
            return ItemStack.EMPTY
        if (items.size < maxSlots) {
            items.add(stack)
            return ItemStack.EMPTY
        }
        return stack
    }

    override fun setChanged() {
    }
}