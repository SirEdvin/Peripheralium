package site.siredvin.peripheralium

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import site.siredvin.peripheralium.forge.ForgeIngredients
import site.siredvin.peripheralium.forge.ForgeLibPlatform
import site.siredvin.peripheralium.forge.ForgePeripheraliumPlatform
import site.siredvin.peripheralium.storages.ForgeStorageUtils
import site.siredvin.peripheralium.storages.energy.EnergyStorageExtractor
import site.siredvin.peripheralium.storages.fluid.FluidStorageExtractor
import site.siredvin.peripheralium.storages.item.ItemStorageExtractor
import site.siredvin.peripheralium.xplat.LibCommonHooks
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(PeripheraliumCore.MOD_ID)
@EventBusSubscriber(modid = PeripheraliumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgePeripheralium {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(ForgeRegistries.BLOCKS, PeripheraliumCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, PeripheraliumCore.MOD_ID)
    val creativeTabRegistry: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), PeripheraliumCore.MOD_ID)

    init {
        PeripheraliumCore.configure(ForgeLibPlatform, ForgePeripheraliumPlatform, ForgeIngredients)
        // Register extract storages
        ItemStorageExtractor.addStorageExtractor(ForgeStorageUtils::extractStorageFromBlock)
        FluidStorageExtractor.addFluidStorageExtractor(ForgeStorageUtils::extractFluidStorageFromBlock)
        EnergyStorageExtractor.addEnergyStorageExtractor(ForgeStorageUtils::extractEnergyStorageFromBlock)
        EnergyStorageExtractor.addEnergyStorageExtractor(ForgeStorageUtils::extractEnergyStorageFromItem)
        val eventBus = MOD_CONTEXT.getKEventBus()
        LibCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
        creativeTabRegistry.register(eventBus)
    }

    fun sayHi() {
    }
}
