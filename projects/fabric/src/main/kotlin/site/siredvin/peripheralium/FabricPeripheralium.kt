package site.siredvin.peripheralium

import net.fabricmc.api.ModInitializer
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.fabric.FabricIngredients
import site.siredvin.peripheralium.fabric.FabricLibPlatform
import site.siredvin.peripheralium.fabric.FabricPeripheraliumPlatform
import site.siredvin.peripheralium.storage.FabricStorageUtils
import site.siredvin.peripheralium.xplat.LibCommonHooks

object FabricPeripheralium : ModInitializer {

    init {
        PeripheraliumCore.configure(FabricLibPlatform, FabricPeripheraliumPlatform, FabricIngredients)
        // Register extract storages
        ExtractorProxy.addStorageExtractor(FabricStorageUtils::extractStorage)
    }

    fun sayHi() {}

    override fun onInitialize() {
        LibCommonHooks.onRegister()
    }
}
