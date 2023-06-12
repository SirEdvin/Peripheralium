package site.siredvin.peripheralium

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.data.LibText
import site.siredvin.peripheralium.xplat.LibPlatform
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RecipeIngredients

object PeripheraliumCore {
    const val MOD_ID = "peripheralium"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder {
        return builder.icon { Items.PERIPHERALIUM_DUST.get().defaultInstance }
            .title(LibText.CREATIVE_TAB.text)
            .displayItems { _, output ->
                LibPlatform.holder.items.forEach {
                    output.accept(it.get())
                }
                LibPlatform.holder.blocks.forEach {
                    output.accept(it.get())
                }
            }
    }

    fun configure(libPlatform: LibPlatform, platform: PeripheraliumPlatform, ingredients: RecipeIngredients) {
        LibPlatform.configure(libPlatform)
        PeripheraliumPlatform.configure(platform)
        RecipeIngredients.configure(ingredients)
    }
}
