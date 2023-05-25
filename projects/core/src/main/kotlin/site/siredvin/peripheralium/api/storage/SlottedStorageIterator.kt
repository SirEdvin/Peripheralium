package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack

class SlottedStorageIterator(private val storage: SlottedStorage) : Iterator<ItemStack> {
    private var currentIndex = 0
    override fun hasNext(): Boolean {
        return currentIndex < storage.size
    }

    override fun next(): ItemStack {
        val oldIndex = currentIndex
        currentIndex += 1
        return storage.getItem(oldIndex)
    }
}
