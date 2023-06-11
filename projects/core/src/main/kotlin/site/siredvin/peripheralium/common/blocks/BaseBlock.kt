package site.siredvin.peripheralium.common.blocks

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.util.BlockUtil

class BaseBlock @JvmOverloads constructor(
    properties: Properties = BlockUtil.defaultProperties(),
) : Block(properties) {
    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}
