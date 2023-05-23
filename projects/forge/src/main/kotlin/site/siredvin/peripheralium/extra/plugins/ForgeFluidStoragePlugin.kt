package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.detail.ForgeDetailRegistries
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.shared.platform.RegistryWrappers
import dan200.computercraft.shared.util.ArgumentHelpers.getRegistryEntry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.storage.ForgeStorageUtils
import java.util.*
import kotlin.collections.HashMap

/**
 * Copy of https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.19.x/projects/forge/src/main/java/dan200/computercraft/shared/peripheral/generic/methods/FluidMethods.java
 */
open class ForgeFluidStoragePlugin(private val handler: IFluidHandler, private val fluidStorageTransferLimit: Int): IPeripheralPlugin {

    override val additionalType: String
        get() = PeripheralPluginUtils.TYPES.FLUID_STORAGE

    protected open fun fluidInformation(stack: FluidStack): MutableMap<String, Any> {
        return ForgeDetailRegistries.FLUID_STACK.getBasicDetails(stack)
    }

    @LuaFunction(mainThread = true)
    fun tanks(): Map<Int, Map<String, *>> {
        val result: MutableMap<Int, Map<String, *>> = HashMap()
        val size = handler.tanks
        for (i in 0 until size) {
            val stack = handler.getFluidInTank(i)
            if (!stack.isEmpty) result[i + 1] = fluidInformation(stack)
        }
        return result
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pushFluid(computer: IComputerAccess, toName: String, limit: Optional<Int>, fluidName: Optional<String>): Int {
        val fluid =
            if (fluidName.isPresent) getRegistryEntry(fluidName.get(), "fluid", RegistryWrappers.FLUIDS) else null

        // Find location to transfer to
        val location = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")
        val to = ForgeStorageUtils.extractFluidHandler(location.target)
            ?: throw LuaException("Target '$toName' is not an tank")
        val actualLimit: Int = minOf(fluidStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        if (actualLimit <= 0) throw LuaException("Limit must be > 0")
        return if (fluid == null) moveFluid(handler, actualLimit, to) else moveFluid(
            handler,
            FluidStack(fluid, actualLimit),
            to
        )
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pullFluid(computer: IComputerAccess, fromName: String, limit: Optional<Int>, fluidName: Optional<String?>): Int {
        val fluid =
            if (fluidName.isPresent) getRegistryEntry(fluidName.get(), "fluid", RegistryWrappers.FLUIDS) else null

        // Find location to transfer to
        val location = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")
        val from = ForgeStorageUtils.extractFluidHandler(location.target)
            ?: throw LuaException("Target '$fromName' is not an tank")
        val actualLimit: Int = minOf(fluidStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        if (actualLimit <= 0) throw LuaException("Limit must be > 0")
        return if (fluid == null) moveFluid(from, actualLimit, handler) else moveFluid(
            from,
            FluidStack(fluid, actualLimit),
            handler
        )
    }


    private fun moveFluid(from: IFluidHandler, limit: Int, to: IFluidHandler): Int {
        return moveFluid(from, from.drain(limit, IFluidHandler.FluidAction.SIMULATE), limit, to)
    }

    private fun moveFluid(from: IFluidHandler, fluid: FluidStack, to: IFluidHandler): Int {
        return moveFluid(from, from.drain(fluid, IFluidHandler.FluidAction.SIMULATE), fluid.amount, to)
    }

    private fun moveFluid(from: IFluidHandler, extracted: FluidStack?, limit: Int, to: IFluidHandler): Int {
        var trackableExtractor: FluidStack? = extracted
        if (trackableExtractor == null || trackableExtractor.amount <= 0) return 0

        // Limit the amount to extract.
        trackableExtractor = trackableExtractor.copy()
        trackableExtractor.amount = minOf(trackableExtractor.amount, limit)
        val inserted = to.fill(trackableExtractor.copy(), IFluidHandler.FluidAction.EXECUTE)
        if (inserted <= 0) return 0

        // Remove the item from the original inventory. Technically this could fail, but there's little we can do
        // about that.
        trackableExtractor.amount = inserted
        from.drain(trackableExtractor, IFluidHandler.FluidAction.EXECUTE)
        return inserted
    }
}