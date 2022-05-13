package site.siredvin.peripheralium.common.setup

import net.minecraft.world.item.Item
import site.siredvin.peripheralium.common.items.DescriptiveItem
import site.siredvin.peripheralium.util.register

object Items {
    val ITEMS = mutableListOf<Item>()

    val PERIPHERALIUM_BLEND = DescriptiveItem(Item.Properties()).register("peripheralium_blend")
    val PERIPHERALIUM_DUST = DescriptiveItem(Item.Properties()).register("peripheralium_dust")

    fun doSomething() {

    }
}