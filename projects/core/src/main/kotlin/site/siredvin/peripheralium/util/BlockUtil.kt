package site.siredvin.peripheralium.util

import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour

object BlockUtil {
    fun createProperties(
        destroyTime: Float,
        explosionResistance: Float,
        soundType: SoundType?,
        isOcclusion: Boolean = false,
        requiresCorrectToolForDrops: Boolean = false,
    ): BlockBehaviour.Properties {
        var properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of()
            .strength(destroyTime, explosionResistance)
        if (soundType != null) properties = properties.sound(soundType)
        if (!isOcclusion) properties = properties.noOcclusion()
        if (requiresCorrectToolForDrops) {
            properties.requiresCorrectToolForDrops()
        }
        return properties
    }

    fun defaultProperties(destroyTime: Float = 1f, explosionResistance: Float = 5f): BlockBehaviour.Properties {
        return createProperties(
            destroyTime,
            explosionResistance,
            SoundType.STONE,
        )
    }

    fun decoration(destroyTime: Float = 1f, explosionResistance: Float = 5f): BlockBehaviour.Properties {
        return createProperties(
            destroyTime,
            explosionResistance,
            SoundType.WOOD,
        )
    }

    fun unbreakable(): BlockBehaviour.Properties {
        return createProperties(
            -1.0f,
            3600000.0f,
            null,
        )
    }
}
