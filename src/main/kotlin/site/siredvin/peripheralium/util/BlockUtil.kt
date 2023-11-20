package site.siredvin.peripheralium.util

import dan200.computercraft.api.ComputerCraftAPI
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material

object BlockUtil {
    val TURTLE_NORMAL: Block
        get() = Registry.BLOCK.get(ResourceLocation(ComputerCraftAPI.MOD_ID, "turtle_normal"))

    val TURTLE_ADVANCED: Block
        get() = Registry.BLOCK.get(ResourceLocation(ComputerCraftAPI.MOD_ID, "turtle_advanced"))

    fun createProperties(
        material: Material,
        destroyTime: Float,
        explosionResistance: Float,
        soundType: SoundType?,
        isOcclusion: Boolean,
        hasDrop: Boolean,
    ): BlockBehaviour.Properties {
        var properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(material)
            .strength(destroyTime, explosionResistance)
        if (soundType != null) properties = properties.sound(soundType)
        if (!isOcclusion) properties = properties.noOcclusion()
        if (hasDrop)
            properties.requiresCorrectToolForDrops()
        return properties
    }

    fun defaultProperties(): BlockBehaviour.Properties {
        return createProperties(
            Material.METAL,
            1f,
            5f,
            SoundType.METAL,
            false,
            hasDrop = true,
        )
    }

    fun decoration(): BlockBehaviour.Properties {
        return createProperties(
            Material.DECORATION,
            1f,
            5f,
            SoundType.WOOD,
            false,
            hasDrop = true,
        )
    }

    fun unbreakable(): BlockBehaviour.Properties {
        return createProperties(
            Material.DECORATION,
            -1.0f,
            3600000.0f,
            null,
            false,
            hasDrop = false,
        )
    }
}
