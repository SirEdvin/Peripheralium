package site.siredvin.peripheralium.storages

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import site.siredvin.peripheralium.storages.energy.EnergyHandlerWrapper
import site.siredvin.peripheralium.storages.energy.EnergyStorage
import site.siredvin.peripheralium.storages.fluid.FluidStorage
import site.siredvin.peripheralium.storages.fluid.ForgeFluidStorage
import site.siredvin.peripheralium.storages.item.ItemHandlerWrapper
import site.siredvin.peripheralium.storages.item.SlottedItemStorage

object ForgeStorageUtils {
    fun extractEnergyStorage(something: Any?): IEnergyStorage? {
        if (something is BlockEntity && something.isRemoved) return null
        if (something is ICapabilityProvider) {
            val cap: LazyOptional<IEnergyStorage> = something.getCapability(ForgeCapabilities.ENERGY)
            if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
        }
        return something as? IEnergyStorage
    }
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

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): SlottedItemStorage? {
        if (blockEntity == null) {
            return null
        }
        val itemHandler = extractItemHandler(blockEntity) ?: return null
        return ItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): FluidStorage? {
        if (blockEntity == null) return null
        val fluidHandler = extractFluidHandler(blockEntity) ?: return null
        return ForgeFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): EnergyStorage? {
        if (blockEntity == null) return null
        val energyStorage = extractEnergyStorage(blockEntity) ?: return null
        return EnergyHandlerWrapper(energyStorage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromItem(level: Level, stack: ItemStack): EnergyStorage? {
        val energyStorage = extractEnergyStorage(stack) ?: return null
        return EnergyHandlerWrapper(energyStorage)
    }
}
