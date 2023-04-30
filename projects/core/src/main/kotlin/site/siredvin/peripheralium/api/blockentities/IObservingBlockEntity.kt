package site.siredvin.peripheralium.api.blockentities

import net.minecraft.core.BlockPos

interface IObservingBlockEntity {
    fun placed() {}
    fun destroy() {}
    fun onNeighbourChange(neighbour: BlockPos) {}
    fun blockTick() {}
}