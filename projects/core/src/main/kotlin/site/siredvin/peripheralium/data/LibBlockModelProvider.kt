package site.siredvin.peripheralium.data

import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.ModelLocationUtils
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import site.siredvin.peripheralium.common.setup.Blocks

object LibBlockModelProvider {
    fun addModels(generators: BlockModelGenerators) {
        val peripheraliumBlock = Blocks.PERIPHERALIUM_BLOCK.get()

        val model = ModelTemplates.CUBE_ALL.create(
            peripheraliumBlock,
            TextureMapping.cube(peripheraliumBlock).put(TextureSlot.ALL, TextureMapping.getBlockTexture(peripheraliumBlock)),
            generators.modelOutput
        )
        generators.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(
                Blocks.PERIPHERALIUM_BLOCK.get(),
                Variant.variant().with(VariantProperties.MODEL, model)
            )
        )
        generators.delegateItemModel(peripheraliumBlock, ModelLocationUtils.getModelLocation(peripheraliumBlock))
    }
}