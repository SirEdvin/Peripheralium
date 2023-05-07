package site.siredvin.peripheralium

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.minecraftforge.fml.config.ModConfig
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.common.configuration.ConfigHolder
import site.siredvin.peripheralium.fabric.FabricIngredients
import site.siredvin.peripheralium.fabric.FabricPeripheraliumPlatform
import site.siredvin.peripheralium.storage.FabricStorageUtils
import site.siredvin.peripheralium.xplat.LibCommonHooks


object FabricPeripheralium: ModInitializer {

    init {
        PeripheraliumCore.configure(FabricPeripheraliumPlatform(), FabricIngredients)
        // Register extract storages
        ExtractorProxy.addStorageExtractor(FabricStorageUtils::extractStorage)
    }

    fun sayHi() {}

    override fun onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(PeripheraliumCore.MOD_ID, ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)
        LibCommonHooks.onRegister()
    }
}