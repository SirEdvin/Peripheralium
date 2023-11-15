package site.siredvin.peripheralium.common.blocks

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import kotlin.jvm.JvmOverloads

class BaseBlock @JvmOverloads constructor(
    properties: Properties = Properties.of(Material.METAL).strength(1f, 5f).sound(SoundType.METAL).noOcclusion(),
) : Block(properties) {
    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}
