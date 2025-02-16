package site.siredvin.peripheralium.util

import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material

object BlockUtil {
    fun createProperties(
        destroyTime: Float,
        explosionResistance: Float,
        soundType: SoundType?,
        isOcclusion: Boolean = false,
        requiresCorrectToolForDrops: Boolean = false,
        material: Material = Material.STONE,
    ): BlockBehaviour.Properties {
        var properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(material)
            .strength(destroyTime, explosionResistance)
        if (soundType != null) properties = properties.sound(soundType)
        if (!isOcclusion) properties = properties.noOcclusion()
        if (requiresCorrectToolForDrops) {
            properties.requiresCorrectToolForDrops()
        }
        return properties
    }

    fun defaultProperties(destroyTime: Float = 1f, explosionResistance: Float = 5f): BlockBehaviour.Properties = createProperties(
        destroyTime,
        explosionResistance,
        SoundType.STONE,
    )

    fun decoration(destroyTime: Float = 1f, explosionResistance: Float = 5f): BlockBehaviour.Properties = createProperties(
        destroyTime,
        explosionResistance,
        SoundType.WOOD,
    )

    fun unbreakable(): BlockBehaviour.Properties = createProperties(
        -1.0f,
        3600000.0f,
        null,
    )
}
