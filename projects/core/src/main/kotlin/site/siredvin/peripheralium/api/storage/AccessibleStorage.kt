package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack

interface AccessibleStorage : Storage {
    fun getItem(slot: Int): ItemStack
}
