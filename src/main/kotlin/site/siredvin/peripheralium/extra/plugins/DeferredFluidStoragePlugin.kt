package site.siredvin.peripheralium.extra.plugins

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

class DeferredFluidStoragePlugin(level: Level, private val pos: BlockPos, private val side: Direction) : AbstractFluidStoragePlugin(level) {
    override val storage: Storage<FluidVariant>
        get() = FluidStorage.SIDED.find(level, pos, side) ?: Storage.empty()
}
