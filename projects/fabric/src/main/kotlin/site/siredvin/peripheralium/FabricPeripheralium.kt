package site.siredvin.peripheralium

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.fml.config.ModConfig
import site.siredvin.peripheralium.common.configuration.ConfigHolder
import site.siredvin.peripheralium.fabric.FabricIngredients
import site.siredvin.peripheralium.fabric.FabricPeripheraliumPlatform
import site.siredvin.peripheralium.xplat.LibCommonHooks
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RecipeIngredients


object FabricPeripheralium: ModInitializer {

    init {
        PeripheraliumPlatform.configure(FabricPeripheraliumPlatform())
        RecipeIngredients.configure(FabricIngredients)
    }

    override fun onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(PeripheraliumCore.MOD_ID, ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)
        LibCommonHooks.onRegister()
    }
}