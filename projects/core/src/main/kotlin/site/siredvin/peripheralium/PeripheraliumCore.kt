package site.siredvin.peripheralium

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.util.text
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RecipeIngredients

object PeripheraliumCore {
    const val MOD_ID = "peripheralium"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder {
        return builder.icon { Items.PERIPHERALIUM_DUST.get().defaultInstance }
            .title(text(MOD_ID, "creative_tab"))
            .displayItems { _, output ->
                output.accept(Items.PERIPHERALIUM_BLEND.get().defaultInstance)
                output.accept(Items.PERIPHERALIUM_DUST.get().defaultInstance)
                output.accept(Blocks.PERIPHERALIUM_BLOCK.get().asItem().defaultInstance)
            }
    }

    fun configure(platform: PeripheraliumPlatform, ingredients: RecipeIngredients) {
        PeripheraliumPlatform.configure(platform)
        RecipeIngredients.configure(ingredients)
    }
}
