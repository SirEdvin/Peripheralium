package site.siredvin.peripheralium.common.setup

import net.minecraft.world.item.Item
import site.siredvin.peripheralium.common.items.DescriptiveItem
import site.siredvin.peripheralium.xplat.LibPlatform

object Items {
    val PERIPHERALIUM_BLEND = LibPlatform.registerItem("peripheralium_blend") { DescriptiveItem(Item.Properties()) }
    val PERIPHERALIUM_DUST = LibPlatform.registerItem("peripheralium_dust") { DescriptiveItem(Item.Properties()) }

    fun doSomething() {
    }
}
