package site.siredvin.peripheralium.storage

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import site.siredvin.peripheralium.api.storage.SlottedStorage


object ForgeStorageUtils {
    fun extractFluidHandler(something: Any?): IFluidHandler? {
        if (something is BlockEntity && something.isRemoved) return null
        if (something is ICapabilityProvider) {
            val cap: LazyOptional<IFluidHandler> = something.getCapability(ForgeCapabilities.FLUID_HANDLER)
            if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
        }
        return something as? IFluidHandler
    }

    fun extractItemHandler(something: Any?): IItemHandler? {
        if (something is BlockEntity && something.isRemoved) return null
        if (something is ICapabilityProvider) {
            val cap: LazyOptional<IItemHandler> = something.getCapability(ForgeCapabilities.ITEM_HANDLER)
            if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
        }
        return something as? IItemHandler ?: (something as? Container)?.let { InvWrapper(it) }
    }

    fun extractStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): SlottedStorage? {
        if (blockEntity == null)
            return null
        val itemHandler = extractItemHandler(blockEntity) ?: return null
        return ItemHandlerWrapper(itemHandler)
    }

    fun extractStorageFromEntity(level: Level, entity: Entity): SlottedStorage? {
        val itemHandler = extractItemHandler(entity) ?: return null
        return ItemHandlerWrapper(itemHandler)
    }
}