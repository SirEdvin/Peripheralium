package site.siredvin.peripheralium.storages.item

import net.minecraft.world.item.ItemStack

class SlottedStorageIterator(private val storage: SlottedItemStorage) : Iterator<ItemStack> {
    private var currentIndex = 0
    override fun hasNext(): Boolean = currentIndex < storage.size

    override fun next(): ItemStack {
        val oldIndex = currentIndex
        currentIndex += 1
        return storage.getItem(oldIndex)
    }
}
