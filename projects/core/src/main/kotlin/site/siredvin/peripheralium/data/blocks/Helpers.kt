package site.siredvin.peripheralium.data.blocks

import net.minecraft.core.Direction
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.*

val TURTLE_LEFT_UPGRADE = ModelTemplate(
    Optional.of(ResourceLocation(PeripheraliumCore.MOD_ID, "base/upgrade_base_left")),
    Optional.empty(),
    TextureSlot.TEXTURE,
)

val TURTLE_RIGHT_UPGRADE = ModelTemplate(
    Optional.of(ResourceLocation(PeripheraliumCore.MOD_ID, "base/upgrade_base_right")),
    Optional.empty(),
    TextureSlot.TEXTURE,
)

fun createFlatItem(generators: ItemModelGenerators, item: Item, vararg textures: ResourceLocation) {
    createFlatItem(generators, ModelLocationUtils.getModelLocation(item), *textures)
}

fun createFlatItem(
    generators: ItemModelGenerators,
    model: ResourceLocation,
    vararg textures: ResourceLocation,
) {
    if (textures.size > 5) throw IndexOutOfBoundsException("Too many layers")
    if (textures.isEmpty()) throw IndexOutOfBoundsException("Must have at least one texture")
    if (textures.size == 1) {
        ModelTemplates.FLAT_ITEM.create(
            model,
            TextureMapping.layer0(
                textures[0],
            ),
            generators.output,
        )
        return
    }
    val slots = Array(textures.size) { i -> TextureSlot.create("layer$i") }
    val mapping = TextureMapping()
    slots.forEachIndexed { index, textureSlot ->
        mapping.put(textureSlot, textures[index])
    }
    ModelTemplate(Optional.of(ResourceLocation("item/generated")), Optional.empty(), *slots)
        .create(model, mapping, generators.output)
}

fun toYAngle(direction: Direction): VariantProperties.Rotation {
    return when (direction) {
        Direction.NORTH -> VariantProperties.Rotation.R0
        Direction.SOUTH -> VariantProperties.Rotation.R180
        Direction.EAST -> VariantProperties.Rotation.R90
        Direction.WEST -> VariantProperties.Rotation.R270
        else -> {
            VariantProperties.Rotation.R0
            VariantProperties.Rotation.R0
            VariantProperties.Rotation.R180
            VariantProperties.Rotation.R90
            VariantProperties.Rotation.R270
        }
    }
}

fun createHorizontalFacingDispatch(): PropertyDispatch {
    val dispatch = PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
    for (direction in BlockStateProperties.HORIZONTAL_FACING.possibleValues) {
        dispatch.select(
            direction,
            Variant.variant().with(
                VariantProperties.Y_ROT,
                toYAngle(direction),
            ),
        )
    }
    return dispatch
}

fun genericBlock(generators: BlockModelGenerators, block: Block) {
    val model = ModelTemplates.CUBE_ALL.create(
        block,
        TextureMapping.cube(block).put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)),
        generators.modelOutput,
    )
    generators.blockStateOutput.accept(
        MultiVariantGenerator.multiVariant(
            block,
            Variant.variant().with(VariantProperties.MODEL, model),
        ),
    )
    generators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block))
}

fun horizontalOrientedModel(
    generators: BlockModelGenerators,
    block: Block,
    overwriteSide: ResourceLocation? = null,
    overwriteTop: ResourceLocation? = null,
    overwriteBottom: ResourceLocation? = null,
    overwriteFront: ResourceLocation? = null,
): ResourceLocation {
    val textureMapping = TextureMapping.orientableCube(block)
    if (overwriteSide != null) {
        textureMapping.put(TextureSlot.SIDE, overwriteSide)
    }
    if (overwriteBottom != null) {
        textureMapping.put(TextureSlot.BOTTOM, overwriteBottom)
    }
    if (overwriteTop != null) {
        textureMapping.put(TextureSlot.TOP, overwriteTop)
    }
    if (overwriteFront != null) {
        textureMapping.put(TextureSlot.FRONT, overwriteFront)
    }
    return ModelTemplates.CUBE_ORIENTABLE.create(
        block,
        textureMapping,
        generators.modelOutput,
    )
}

fun horizontalOrientatedBlock(
    generators: BlockModelGenerators,
    block: Block,
    model: ResourceLocation? = null,
) {
    generators.blockStateOutput.accept(
        MultiVariantGenerator.multiVariant(
            block,
            Variant.variant()
                .with(VariantProperties.MODEL, model ?: ModelLocationUtils.getModelLocation(block)),
        )
            .with(
                createHorizontalFacingDispatch(),
            ),
    )
    generators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block))
}

fun turtleUpgrades(generators: ItemModelGenerators, block: Block, textureSuffix: String = "", baseID: ResourceLocation? = null) {
    val realBaseID = baseID ?: XplatRegistries.BLOCKS.getKey(block).withPrefix("turtle/")
    TURTLE_RIGHT_UPGRADE.create(
        realBaseID.withSuffix("_right"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block).withSuffix(textureSuffix)),
        generators.output,
    )
    TURTLE_LEFT_UPGRADE.create(
        realBaseID.withSuffix("_left"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block).withSuffix(textureSuffix)),
        generators.output,
    )
}

fun turtleUpgrades(generators: ItemModelGenerators, item: Item, textureSuffix: String = "", baseID: ResourceLocation? = null) {
    val realBaseID = baseID ?: XplatRegistries.ITEMS.getKey(item).withPrefix("turtle/")
    TURTLE_RIGHT_UPGRADE.create(
        realBaseID.withSuffix("_right"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getItemTexture(item).withSuffix(textureSuffix)),
        generators.output,
    )
    TURTLE_LEFT_UPGRADE.create(
        realBaseID.withSuffix("_left"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getItemTexture(item).withSuffix(textureSuffix)),
        generators.output,
    )
}
