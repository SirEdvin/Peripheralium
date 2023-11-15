package site.siredvin.peripheralium.extra.plugins

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.world.level.Level

class FluidStoragePlugin(level: Level, override val storage: Storage<FluidVariant>) : AbstractFluidStoragePlugin(level)
