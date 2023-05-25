package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.common.FabricExtractorProxy
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*
import java.util.function.Predicate

open class FluidStoragePlugin(private val level: Level, private val storage: Storage<FluidVariant>, private val fluidStorageTransferLimit: Int) : IPeripheralPlugin {

    override val additionalType: String
        get() = PeripheralPluginUtils.Type.FLUID_STORAGE

    protected open fun fluidInformation(fluid: StorageView<FluidVariant>): MutableMap<String, Any> {
        return mutableMapOf(
            "name" to BuiltInRegistries.FLUID.getKey(fluid.resource.fluid).toString(),
            "amount" to fluid.amount / PeripheraliumPlatform.fluidCompactDivider,
            "capacity" to fluid.capacity / PeripheraliumPlatform.fluidCompactDivider,
        )
    }

    @LuaFunction(mainThread = true)
    fun tanks(): List<Map<String, *>> {
        val data: MutableList<Map<String, *>> = mutableListOf()
        storage.iterator().forEach {
            data.add(fluidInformation(it))
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
            if (fluid.isSame(Fluids.EMPTY)) {
                throw LuaException("There is no fluid ${fluidName.get()}")
            }
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(fluidStorageTransferLimit.toLong(), limit.map { it * PeripheraliumPlatform.fluidCompactDivider.toLong() }.orElse(Long.MAX_VALUE))
        return StorageUtil.move(storage, toStorage, predicate, realLimit, null) / PeripheraliumPlatform.fluidCompactDivider
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
            if (fluid.isSame(Fluids.EMPTY)) {
                throw LuaException("There is no fluid ${fluidName.get()}")
            }
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(fluidStorageTransferLimit.toLong(), limit.map { it * PeripheraliumPlatform.fluidCompactDivider.toLong() }.orElse(Long.MAX_VALUE))
        return StorageUtil.move(fromStorage, storage, predicate, realLimit, null) / PeripheraliumPlatform.fluidCompactDivider
    }
}
