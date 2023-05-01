package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack

interface TargetableStorage {
    /**
     * Minimal storage abstraction, that can be used to store item in
     * Mostly used for item_storage and inventory plugins
     */
    fun storeItem(stack: ItemStack): ItemStack
    fun setChanged()
}
