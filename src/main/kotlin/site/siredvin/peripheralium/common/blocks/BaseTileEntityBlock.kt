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
import site.siredvin.peripheralium.api.blocks.IBlockObservingTileEntity
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import java.util.*

abstract class BaseTileEntityBlock<T: BlockEntity>(
    private val belongToTickingEntity: Boolean,
    properties: Properties = Properties.of(Material.METAL).strength(1f, 5f).sound(SoundType.METAL).noOcclusion()
): BaseEntityBlock(properties) {

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (level.isClientSide || !belongToTickingEntity)
            return null
        return BlockEntityTicker { _, _, _, entity ->
            if (entity is IPeripheralTileEntity) {
                entity.handleTick(level, state, type)
            }
        }
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        neighbourBlock: Block,
        neighbourPos: BlockPos,
        bl: Boolean
    ) {
        super.neighborChanged(blockState, level, blockPos, neighbourBlock, neighbourPos, bl)
        val tile = level.getBlockEntity(blockPos)
        if (tile is IBlockObservingTileEntity)
            tile.onNeighbourChange(neighbourPos)
    }

    override fun tick(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, random: Random) {
        super.tick(blockState, serverLevel, blockPos, random)
        val tile = serverLevel.getBlockEntity(blockPos)
        if (tile is IBlockObservingTileEntity)
            tile.blockTick()
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        newState: BlockState,
        bl: Boolean
    ) {
        super.onPlace(blockState, level, blockPos, newState, bl)
        if (newState.block === this) {
            val tile = level.getBlockEntity(blockPos)
            if (tile is IBlockObservingTileEntity)
                tile.placed()
        }
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        replace: BlockState,
        bl: Boolean
    ) {
        if (blockState.block === replace.block)
            return
        val tile = level.getBlockEntity(blockPos)
        super.onRemove(blockState, level, blockPos, replace, bl)
        if (tile is IBlockObservingTileEntity)
            tile.destroy()
    }
}