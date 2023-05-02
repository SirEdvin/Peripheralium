package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface SlottedStorage: Storage, TargetableSlottedStorage {
    fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack

    fun getItem(slot: Int): ItemStack

    fun canPlaceItem(slot: Int, item: ItemStack): Boolean

    override fun getItems(): Iterator<ItemStack> {
        return SlottedStorageIterator(this)
    }

    override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack {
        return takeItems(limit, 0, size - 1, predicate)
    }
}