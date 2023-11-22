package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.level.Level
import site.siredvin.peripheralium.storages.fluid.FluidStorage

open class FluidStoragePlugin(level: Level, override val storage: FluidStorage, fluidStorageTransferLimit: Int) : AbstractFluidStoragePlugin(level, fluidStorageTransferLimit)
