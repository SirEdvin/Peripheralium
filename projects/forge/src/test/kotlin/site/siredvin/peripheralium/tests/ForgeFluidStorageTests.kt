package site.siredvin.peripheralium.tests

import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import site.siredvin.peripheralium.storage.DummyFluidStorage
import site.siredvin.peripheralium.storages.fluid.*

internal class CombinedTanks(private val tanks: List<IFluidTank>) : IFluidHandler {
    override fun getTanks(): Int {
        return tanks.size
    }

    override fun getFluidInTank(tank: Int): net.minecraftforge.fluids.FluidStack {
        return tanks[tank].fluid
    }

    override fun getTankCapacity(tank: Int): Int {
        return tanks[tank].capacity
    }

    override fun isFluidValid(tank: Int, stack: net.minecraftforge.fluids.FluidStack): Boolean {
        return tanks[tank].isFluidValid(stack)
    }

    override fun fill(resource: net.minecraftforge.fluids.FluidStack, action: IFluidHandler.FluidAction): Int {
        val slidingStack = resource.copy()
        var filledAmount = 0
        tanks.forEach {
            if (it.isFluidValid(slidingStack)) {
                val partFilledAmount = it.fill(slidingStack, action)
                filledAmount += partFilledAmount
                slidingStack.shrink(filledAmount)
            }
        }
        return filledAmount
    }

    override fun drain(
        resource: net.minecraftforge.fluids.FluidStack,
        action: IFluidHandler.FluidAction,
    ): net.minecraftforge.fluids.FluidStack {
        val collectedStack = resource.copyWithCount(0)
        val slidingDrain = resource.copy()
        tanks.forEach {
            val drainedStack = it.drain(slidingDrain, action)
            collectedStack.grow(drainedStack.amount)
            slidingDrain.shrink(drainedStack.amount)
        }
        return collectedStack
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): net.minecraftforge.fluids.FluidStack {
        var slidingMaxDrain = maxDrain
        var currentStack = net.minecraftforge.fluids.FluidStack.EMPTY
        tanks.forEach {
            if (currentStack.isEmpty) {
                val drainedStack = it.drain(slidingMaxDrain, action)
                if (!drainedStack.isEmpty) {
                    currentStack = drainedStack
                    slidingMaxDrain -= drainedStack.amount
                }
            } else {
                val drainedStack = it.drain(currentStack.copyWithCount(slidingMaxDrain), action)
                currentStack.grow(drainedStack.amount)
                slidingMaxDrain -= drainedStack.amount
            }
        }
        return currentStack
    }
}

@WithMinecraft
internal class ForgeFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage {
        return ForgeFluidStorage(
            CombinedTanks(
                fluids.map { stack ->
                    val storage = FluidTank(1000)
                    if (!stack.isEmpty) {
                        storage.fill(stack.toForge(), IFluidHandler.FluidAction.EXECUTE)
                    }
                    return@map storage
                },
            ),
        )
    }
}

@WithMinecraft
internal class ForgeDummyFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage {
        if (secondary) {
            return DummyFluidStorage(fluids.size, fluids)
        }
        return ForgeFluidStorage(
            CombinedTanks(
                fluids.map { stack ->
                    val storage = FluidTank(1000)
                    if (!stack.isEmpty) {
                        storage.fill(stack.toForge(), IFluidHandler.FluidAction.EXECUTE)
                    }
                    return@map storage
                },
            ),
        )
    }
}
