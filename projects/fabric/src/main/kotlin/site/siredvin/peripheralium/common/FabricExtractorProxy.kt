package site.siredvin.peripheralium.common

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

object FabricExtractorProxy {
    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorage(level: Level, obj: Any?): Storage<FluidVariant>? {
        if (obj is BlockEntity) {
            if (obj.isRemoved) {
                return null
            }
            return FluidStorage.SIDED.find(obj.level, obj.blockPos, null)
        }
        return null
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractItemStorage(level: Level, obj: Any?): Storage<ItemVariant>? {
        if (obj is BlockEntity) {
            if (obj.isRemoved) {
                return null
            }
            return net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(obj.level, obj.blockPos, null)
        }
        return null
    }
}
