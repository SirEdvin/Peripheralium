package site.siredvin.peripheralium.forge

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.ForgePeripheralium
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.xplat.LibPlatform
import java.util.function.Supplier

object ForgeLibPlatform : LibPlatform {
    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        return ForgePeripheralium.itemsRegistry.register(key.path, item)
    }

    override fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        PeripheraliumCore.LOGGER.warn("Register block")
        val blockRegister = ForgePeripheralium.blocksRegistry.register(key.path, block)
        ForgePeripheralium.itemsRegistry.register(key.path) { itemFactory(blockRegister.get()) }
        return blockRegister
    }

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        return ForgePeripheralium.creativeTabRegistry.register(key.path) { tab }
    }
}
