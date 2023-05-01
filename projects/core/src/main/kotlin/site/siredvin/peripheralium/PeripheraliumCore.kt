package site.siredvin.peripheralium

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.api.blockentities.IObservingBlockEntity
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.common.MinecartHelpers
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RecipeIngredients


object PeripheraliumCore {
    const val MOD_ID = "peripheralium"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configure(platform: PeripheraliumPlatform, ingredients: RecipeIngredients) {
        PeripheraliumPlatform.configure(platform)
        RecipeIngredients.configure(ingredients)
        ExtractorProxy.addStorageExtractor(MinecartHelpers::minecartExtractor)
    }
}