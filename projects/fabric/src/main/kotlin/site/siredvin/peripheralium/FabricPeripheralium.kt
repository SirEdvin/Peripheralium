package site.siredvin.peripheralium

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import site.siredvin.peripheralium.fabric.*
import site.siredvin.peripheralium.storages.FabricStorageUtils
import site.siredvin.peripheralium.storages.fluid.FluidStorageExtractor
import site.siredvin.peripheralium.storages.item.ItemStorageExtractor
import site.siredvin.peripheralium.xplat.LibCommonHooks

object FabricPeripheralium : ModInitializer {

    init {
        PeripheraliumCore.configure(FabricLibInnerPlatform, FabricPeripheraliumPlatform, FabricIngredients, FabricXplatTags)
        // Register extract storages
        ItemStorageExtractor.addStorageExtractor(FabricStorageUtils::extractStorage)
        FluidStorageExtractor.addFluidStorageExtractor(FabricStorageUtils::extractFluidStorage)
        FluidStorageExtractor.addFluidStorageExtractor(FabricStorageUtils::extractFluidStorageFromItem)
        ServerWorldEvents.LOAD.register(
            ServerWorldEvents.Load { server, _ ->
                FabricPeripheraliumPlatform.minecraftServer = server
            },
        )
    }

    fun sayHi() {}

    override fun onInitialize() {
        LibCommonHooks.onRegister()
    }
}
