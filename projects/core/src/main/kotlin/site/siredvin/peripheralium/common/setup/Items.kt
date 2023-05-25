package site.siredvin.peripheralium.common.setup

import net.minecraft.world.item.Item
import site.siredvin.peripheralium.common.items.DescriptiveItem
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

object Items {
    val PERIPHERALIUM_BLEND = PeripheraliumPlatform.registerItem("peripheralium_blend") { DescriptiveItem(Item.Properties()) }
    val PERIPHERALIUM_DUST = PeripheraliumPlatform.registerItem("peripheralium_dust") { DescriptiveItem(Item.Properties()) }

    fun doSomething() {
    }
}
