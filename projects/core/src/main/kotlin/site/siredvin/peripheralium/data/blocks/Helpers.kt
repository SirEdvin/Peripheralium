package site.siredvin.peripheralium.data.blocks

import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplate
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import java.util.*

fun createFlatItem(
    generators: ItemModelGenerators,
    model: ResourceLocation,
    vararg textures: ResourceLocation
) {
    if (textures.size > 5) throw IndexOutOfBoundsException("Too many layers")
    if (textures.isEmpty()) throw IndexOutOfBoundsException("Must have at least one texture")
    if (textures.size == 1) {
        ModelTemplates.FLAT_ITEM.create(
            model, TextureMapping.layer0(
                textures[0]
            ), generators.output
        )
        return
    }
    val slots = arrayOfNulls<TextureSlot>(textures.size)
    val mapping = TextureMapping()
    for (i in textures.indices) {
        slots[i] = TextureSlot.create("layer$i")
        val slot = slots[i]
        mapping.put(slot, textures[i])
    }
    ModelTemplate(Optional.of(ResourceLocation("item/generated")), Optional.empty(), *slots)
        .create(model, mapping, generators.output)
}