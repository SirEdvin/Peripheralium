package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack

interface TargetableSlottedStorage: TargetableStorage {
    /**
     * Targetable storage with slots, mostly used for inventory plugin logic
     */
    val size: Int

    fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack

    fun storeItem(stack: ItemStack, startSlot: Int): ItemStack {
        return storeItem(stack, startSlot, size - 1)
    }
    override fun storeItem(stack: ItemStack): ItemStack {
        return storeItem(stack, 0, size - 1)
    }
}