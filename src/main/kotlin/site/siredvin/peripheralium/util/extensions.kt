package site.siredvin.peripheralium.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import site.siredvin.peripheralium.Peripheralium
import site.siredvin.peripheralium.common.setup.Items

fun <T: Item> T.register(name: String): T {
    Registry.register(Registry.ITEM, ResourceLocation(Peripheralium.MOD_ID, name), this)
    Items.ITEMS.add(this)
    return this
}