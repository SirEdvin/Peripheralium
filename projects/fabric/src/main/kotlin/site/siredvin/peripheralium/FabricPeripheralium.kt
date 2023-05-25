package site.siredvin.peripheralium

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.resources.ResourceLocation
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.fabric.FabricIngredients
import site.siredvin.peripheralium.fabric.FabricPeripheraliumPlatform
import site.siredvin.peripheralium.storage.FabricStorageUtils
import site.siredvin.peripheralium.xplat.LibCommonHooks

object FabricPeripheralium : ModInitializer {

    init {
        PeripheraliumCore.configure(FabricPeripheraliumPlatform(), FabricIngredients)
        PeripheraliumCore.configureCreativeTab(
            FabricItemGroup.builder(
                ResourceLocation(PeripheraliumCore.MOD_ID, "tab"),
            ),
        ).build()
        // Register extract storages
        ExtractorProxy.addStorageExtractor(FabricStorageUtils::extractStorage)
    }

    fun sayHi() {}

    override fun onInitialize() {
        LibCommonHooks.onRegister()
    }
}
