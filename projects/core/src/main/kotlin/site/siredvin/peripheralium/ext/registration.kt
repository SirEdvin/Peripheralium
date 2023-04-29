package site.siredvin.peripheralium.ext

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.function.Supplier

fun <T: Item> T.register(name: String): Supplier<T> {
    return PeripheraliumPlatform.registerItem(ResourceLocation(PeripheraliumCore.MOD_ID, name), this)
}

fun <T: Block> T.register(name: String, itemFactory: (Block) -> (Item) = { block -> DescriptiveBlockItem(block, Item.Properties()) }): Supplier<T> {
    return PeripheraliumPlatform.registerBlock(ResourceLocation(PeripheraliumCore.MOD_ID, name), this, itemFactory)
}
