package site.siredvin.peripheralium.forge

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.peripheralium.ForgePeripheralium
import site.siredvin.peripheralium.PeripheraliumCore

object ForgeLibInnerPlatform : ForgeBaseInnerPlatform() {
    override val modID: String
        get() = PeripheraliumCore.MOD_ID

    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgePeripheralium.blocksRegistry

    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgePeripheralium.itemsRegistry

    override val creativeTabRegistry: DeferredRegister<CreativeModeTab>
        get() = ForgePeripheralium.creativeTabRegistry
}
