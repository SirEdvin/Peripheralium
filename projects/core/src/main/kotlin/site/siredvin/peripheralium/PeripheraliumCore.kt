package site.siredvin.peripheralium

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RecipeIngredients


object PeripheraliumCore {
    const val MOD_ID = "peripheralium"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configure(platform: PeripheraliumPlatform, ingredients: RecipeIngredients) {
        PeripheraliumPlatform.configure(platform)
        RecipeIngredients.configure(ingredients)
    }
}