package site.siredvin.peripheralium.storage

import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.Storage

interface TestableStorage: Storage {
    fun getItem(slot: Int): ItemStack
}