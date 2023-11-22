package site.siredvin.peripheralium.extra.plugins

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.storages.fluid.EmptyFluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStorageExtractor

open class DeferredFluidStoragePlugin(level: Level, private val pos: BlockPos, private val side: Direction, fluidStorageTransferLimit: Int) : AbstractFluidStoragePlugin(level, fluidStorageTransferLimit) {
    override val storage: FluidStorage
        get() = FluidStorageExtractor.extractFluidStorage(level, pos, level.getBlockEntity(pos)) ?: EmptyFluidStorage
}
