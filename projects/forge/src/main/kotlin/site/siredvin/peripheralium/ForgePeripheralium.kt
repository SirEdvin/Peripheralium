package site.siredvin.peripheralium

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.event.CreativeModeTabEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.forge.ForgeIngredients
import site.siredvin.peripheralium.forge.ForgePeripheraliumPlatform
import site.siredvin.peripheralium.storage.ForgeStorageUtils
import site.siredvin.peripheralium.xplat.LibCommonHooks
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(PeripheraliumCore.MOD_ID)
@EventBusSubscriber(modid = PeripheraliumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgePeripheralium {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(ForgeRegistries.BLOCKS, PeripheraliumCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, PeripheraliumCore.MOD_ID)

    init {
        PeripheraliumCore.configure(ForgePeripheraliumPlatform(), ForgeIngredients)
        // Register extract storages
        ExtractorProxy.addStorageExtractor(ForgeStorageUtils::extractStorageFromBlock)
        val eventBus = MOD_CONTEXT.getKEventBus()
        LibCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
    }

    fun sayHi() {
    }

    @SubscribeEvent
    fun registerCreativeTab(event: CreativeModeTabEvent.Register) {
        event.registerCreativeModeTab(ResourceLocation(PeripheraliumCore.MOD_ID, "tab"), PeripheraliumCore::configureCreativeTab)
    }
}
