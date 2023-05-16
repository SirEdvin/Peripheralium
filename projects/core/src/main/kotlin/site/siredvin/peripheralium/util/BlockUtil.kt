package site.siredvin.peripheralium.util

import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material

object BlockUtil {
    fun createProperties(
        material: Material,
        destroyTime: Float,
        explosionResistance: Float,
        soundType: SoundType?,
        isOcclusion: Boolean = true,
        requiresCorrectToolForDrops: Boolean = false
    ): BlockBehaviour.Properties {
        var properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(material)
            .strength(destroyTime, explosionResistance)
        if (soundType != null) properties = properties.sound(soundType)
        if (!isOcclusion) properties = properties.noOcclusion()
        if (requiresCorrectToolForDrops)
            properties.requiresCorrectToolForDrops()
        return properties
    }

    fun defaultProperties(): BlockBehaviour.Properties {
        return createProperties(
            Material.STONE, 1f, 5f, SoundType.STONE,
        )
    }

    fun decoration(): BlockBehaviour.Properties {
        return createProperties(
            Material.DECORATION, 1f, 5f, SoundType.WOOD,
        )
    }

    fun unbreakable(): BlockBehaviour.Properties {
        return createProperties(
            Material.DECORATION, -1.0f, 3600000.0f, null,
        )
    }
}