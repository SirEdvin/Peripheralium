package site.siredvin.peripheralium.ext

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.Peripheralium
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items

fun <T: Item> T.register(name: String): T {
    Registry.register(Registry.ITEM, ResourceLocation(Peripheralium.MOD_ID, name), this)
    Items.ITEMS.add(this)
    return this
}

fun <T: Block> T.register(name: String, itemFactory: (Block) -> (Item) = { block -> DescriptiveBlockItem(block, Item.Properties()) }): T {
    Registry.register(Registry.BLOCK, ResourceLocation(Peripheralium.MOD_ID, name), this)
    Registry.register(Registry.ITEM, ResourceLocation(Peripheralium.MOD_ID, name), itemFactory(this))
    Blocks.BLOCKS.add(this)
    return this
}
