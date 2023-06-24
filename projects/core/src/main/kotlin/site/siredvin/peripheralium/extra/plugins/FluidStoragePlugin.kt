package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.storages.fluid.FluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStorageExtractor
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.*
import java.util.function.Predicate

open class FluidStoragePlugin(private val level: Level, private val storage: FluidStorage, private val fluidStorageTransferLimit: Int) : IPeripheralPlugin {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.FLUID_STORAGE

    protected open fun fluidInformation(fluid: FluidStack): MutableMap<String, Any?> {
        val baseInformation = mutableMapOf<String, Any?>(
            "name" to XplatRegistries.FLUIDS.getKey(fluid.fluid).toString(),
            "amount" to fluid.amount,
        )
        if (fluid.tag != null) {
            baseInformation["nbt"] = PeripheraliumPlatform.nbtHash(fluid.tag!!)
        }
        return baseInformation
    }

    @LuaFunction(mainThread = true)
    fun tanks(): List<Map<String, *>> {
        val data: MutableList<Map<String, *>> = mutableListOf()
        storage.getFluids().forEach {
            data.add(fluidInformation(it))
        }
        return data
    }

    @LuaFunction(mainThread = true)
    fun pushFluid(computer: IComputerAccess, toName: String, limit: Optional<Long>, fluidName: Optional<String>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = FluidStorageExtractor.extractFluidSinkFromUnknown(level, location.target)
            ?: throw LuaException("Target '$toName' is not an fluid inventory")

        val predicate: Predicate<FluidStack> = if (fluidName.isEmpty) {
            Predicate { true }
        } else {
            val fluid = XplatRegistries.FLUIDS.get(ResourceLocation(fluidName.get()))
            if (fluid.isSame(Fluids.EMPTY)) {
                throw LuaException("There is no fluid ${fluidName.get()}")
            }
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(fluidStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, predicate).toDouble()
    }

    @LuaFunction(mainThread = true)
    fun pullFluid(computer: IComputerAccess, fromName: String, limit: Optional<Long>, fluidName: Optional<String>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = FluidStorageExtractor.extractFluidStorageFromUnknown(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an fluid inventory")

        val predicate: Predicate<FluidStack> = if (fluidName.isEmpty) {
            Predicate { true }
        } else {
            val fluid = XplatRegistries.FLUIDS.get(ResourceLocation(fluidName.get()))
            if (fluid.isSame(Fluids.EMPTY)) {
                throw LuaException("There is no fluid ${fluidName.get()}")
            }
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(fluidStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, predicate).toDouble()
    }
}
