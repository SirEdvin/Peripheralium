package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.common.FabricExtractorProxy
import site.siredvin.peripheralium.common.configuration.PeripheraliumConfig
import java.util.*
import java.util.function.Predicate

class FluidStoragePlugin(private val level: Level, private val storage: Storage<FluidVariant>): IPeripheralPlugin {

    companion object {
        const val FORGE_COMPACT_DEVIDER = 81.0
    }

    override val additionalType: String
        get() = PeripheralPluginUtils.TYPES.FLUID_STORAGE

    @LuaFunction(mainThread = true)
    fun tanks(): List<Map<String, *>> {
        val data: MutableList<Map<String, *>> = mutableListOf()
        storage.iterator().forEach {
            data.add(hashMapOf(
                "name" to BuiltInRegistries.FLUID.getKey(it.resource.fluid).toString(),
                "amount" to it.amount / FORGE_COMPACT_DEVIDER,
                "capacity" to it.capacity / FORGE_COMPACT_DEVIDER
            ))
        }
        return data
    }

    @LuaFunction(mainThread = true)
    fun pushFluid(computer: IComputerAccess, toName: String, limit: Optional<Long>, fluidName: Optional<String>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = FabricExtractorProxy.extractFluidStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an fluid inventory")

        val predicate: Predicate<FluidVariant> = if (fluidName.isEmpty) {
            Predicate { true }
        } else {
            val fluid = BuiltInRegistries.FLUID.get(ResourceLocation(fluidName.get()))
            if (fluid.isSame(Fluids.EMPTY))
                throw LuaException("There is no fluid ${fluidName.get()}")
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(PeripheraliumConfig.fluidStorageTransferLimit.toLong(), limit.map { it * FORGE_COMPACT_DEVIDER.toLong() }.orElse(Long.MAX_VALUE))
        return StorageUtil.move(storage, toStorage, predicate, realLimit, null) / FORGE_COMPACT_DEVIDER
    }

    @LuaFunction(mainThread = true)
    fun pullFluid(computer: IComputerAccess, fromName: String, limit: Optional<Long>, fluidName: Optional<String>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = FabricExtractorProxy.extractFluidStorage(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an fluid inventory")

        val predicate: Predicate<FluidVariant> = if (fluidName.isEmpty) {
            Predicate { true }
        } else {
            val fluid = BuiltInRegistries.FLUID.get(ResourceLocation(fluidName.get()))
            if (fluid.isSame(Fluids.EMPTY))
                throw LuaException("There is no fluid ${fluidName.get()}")
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(PeripheraliumConfig.fluidStorageTransferLimit.toLong(), limit.map { it * FORGE_COMPACT_DEVIDER.toLong() }.orElse(Long.MAX_VALUE))
        return StorageUtil.move(fromStorage, storage, predicate, realLimit, null) / FORGE_COMPACT_DEVIDER
    }
}