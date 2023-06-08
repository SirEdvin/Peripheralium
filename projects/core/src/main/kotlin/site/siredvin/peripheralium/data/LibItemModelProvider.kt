package site.siredvin.peripheralium.data

import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import site.siredvin.peripheralium.common.setup.Items

object LibItemModelProvider {
    fun addModels(generators: ItemModelGenerators) {
        val peripheraliumDust = Items.PERIPHERALIUM_DUST.get()
        val periphaliumBlend = Items.PERIPHERALIUM_BLEND.get()

        generators.generateFlatItem(peripheraliumDust, ModelTemplates.FLAT_ITEM)
        generators.generateFlatItem(periphaliumBlend, ModelTemplates.FLAT_ITEM)
    }
}
