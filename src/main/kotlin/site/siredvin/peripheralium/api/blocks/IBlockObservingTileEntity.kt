package site.siredvin.peripheralium.api.blocks

import net.minecraft.core.BlockPos

interface IBlockObservingTileEntity {
    fun placed() {}
    fun destroy() {}
    fun onNeighbourChange(neighbour: BlockPos) {}
    fun onNeighbourTileEntityChange(neighbour: BlockPos) {}
    fun onChunkUnloaded() {}
    fun blockTick() {}
}