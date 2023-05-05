package site.siredvin.peripheralium

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.common.configuration.ConfigHolder
import site.siredvin.peripheralium.forge.ForgeIngredients
import site.siredvin.peripheralium.forge.ForgePeripheraliumPlatform
import site.siredvin.peripheralium.storage.ForgeStorageUtils
import site.siredvin.peripheralium.xplat.LibCommonHooks
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RecipeIngredients
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT


@Mod(PeripheraliumCore.MOD_ID)
object ForgePeripheralium {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(ForgeRegistries.BLOCKS, PeripheraliumCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, PeripheraliumCore.MOD_ID)

    init {
        PeripheraliumCore.configure(ForgePeripheraliumPlatform(), ForgeIngredients)
        val context = ModLoadingContext.get()
        context.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)
        // Register extract storages
        ExtractorProxy.addStorageExtractor(ForgeStorageUtils::extractStorage)
        val eventBus = MOD_CONTEXT.getKEventBus()
        eventBus.addListener(this::commonSetup)
        LibCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
    }

    fun commonSetup(event: FMLCommonSetupEvent) {
    }
}