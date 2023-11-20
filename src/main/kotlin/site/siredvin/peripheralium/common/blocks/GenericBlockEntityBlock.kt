package site.siredvin.peripheralium.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.Material
import java.util.function.Supplier

class GenericBlockEntityBlock<T : BlockEntity>(
    private val blockEntityTypeSup: Supplier<BlockEntityType<T>>,
    private val isRotatable: Boolean,
    belongToTickingEntity: Boolean = false,
    properties: Properties = Properties.of(Material.METAL).strength(1f, 5f).sound(SoundType.METAL).noOcclusion(),
) : BaseTileEntityBlock<T>(belongToTickingEntity, properties) {

    companion object {
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return blockEntityTypeSup.get().create(pos, state)
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return if (isRotatable) {
            state.setValue(
                FACING,
                rot.rotate(state.getValue(FACING)),
            )
        } else {
            state
        }
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return if (isRotatable) state.rotate(mirrorIn.getRotation(state.getValue(FACING))) else state
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return if (isRotatable) {
            defaultBlockState().setValue(
                FACING,
                context.horizontalDirection.opposite,
            )
        } else {
            defaultBlockState()
        }
    }
}
