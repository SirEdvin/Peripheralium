package site.siredvin.peripheralium.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import site.siredvin.peripheralium.api.blockentities.IObservingBlockEntity
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import java.util.*

abstract class BaseTileEntityBlock<T : BlockEntity>(
    private val belongToTickingEntity: Boolean,
    properties: Properties = Properties.of(Material.METAL).strength(1f, 5f).sound(SoundType.METAL).noOcclusion(),
) : BaseEntityBlock(properties) {

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun <T : BlockEntity> getTicker(
        tickerLevel: Level,
        tickerState: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (tickerLevel.isClientSide || !belongToTickingEntity) {
            return null
        }
        return BlockEntityTicker { level, pos, state, entity ->
            if (entity is IPeripheralTileEntity) {
                entity.handleTick(level, pos, state)
            }
        }
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        neighbourBlock: Block,
        neighbourPos: BlockPos,
        bl: Boolean,
    ) {
        @Suppress("DEPRECATION")
        super.neighborChanged(blockState, level, blockPos, neighbourBlock, neighbourPos, bl)
        val tile = level.getBlockEntity(blockPos)
        if (tile is IObservingBlockEntity) {
            tile.onNeighbourChange(neighbourPos)
        }
    }

    override fun tick(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, random: Random) {
        @Suppress("DEPRECATION")
        super.tick(blockState, serverLevel, blockPos, random)
        val tile = serverLevel.getBlockEntity(blockPos)
        if (tile is IObservingBlockEntity) {
            tile.blockTick()
        }
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        newState: BlockState,
        bl: Boolean,
    ) {
        @Suppress("DEPRECATION")
        super.onPlace(blockState, level, blockPos, newState, bl)
        if (newState.block === this) {
            val tile = level.getBlockEntity(blockPos)
            if (tile is IObservingBlockEntity) {
                tile.placed()
            }
        }
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        replace: BlockState,
        bl: Boolean,
    ) {
        if (blockState.block === replace.block) {
            return
        }
        val tile = level.getBlockEntity(blockPos)
        @Suppress("DEPRECATION")
        super.onRemove(blockState, level, blockPos, replace, bl)
        if (tile is IObservingBlockEntity) {
            tile.destroy()
        }
    }
}
