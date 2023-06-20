package site.siredvin.peripheralium.storages.item

import net.minecraft.world.item.ItemStack

interface AccessibleItemStorage : ItemStorage {
    fun getItem(slot: Int): ItemStack
}
