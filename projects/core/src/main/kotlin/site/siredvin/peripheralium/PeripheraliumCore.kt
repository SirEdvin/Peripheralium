package site.siredvin.peripheralium

import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.xplat.*

object PeripheraliumCore {
    const val MOD_ID = "peripheralium"

    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(): CreativeTabProvider = object : CreativeTabProvider {

        override fun appendItems(items: MutableList<ItemStack>): List<ItemStack> {
            LibPlatform.holder.items.forEach { items.add(it.get().defaultInstance) }
            LibPlatform.holder.blocks.forEach { items.add(it.get().asItem().defaultInstance) }
            return items
        }

        override fun makeIcon(): ItemStack = Items.PERIPHERALIUM_DUST.get().defaultInstance
    }

    fun configure(libPlatform: BaseInnerPlatform, platform: PeripheraliumPlatform, ingredients: RecipeIngredients, tags: XplatTags) {
        LibPlatform.configure(libPlatform)
        PeripheraliumPlatform.configure(platform)
        RecipeIngredients.configure(ingredients)
        XplatTags.configure(tags)
    }
}
