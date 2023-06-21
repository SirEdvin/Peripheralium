package site.siredvin.peripheralium.storages.fluid

import net.minecraftforge.fluids.capability.IFluidHandler
import java.util.function.Predicate
import net.minecraftforge.fluids.FluidStack as ForgeFluidStack

class ForgeFluidStorage(private val handler: IFluidHandler) : FluidStorage {
    override fun getFluids(): Iterator<FluidStack> {
        return (0 until handler.tanks).map {
            handler.getFluidInTank(it).toVanilla()
        }.iterator()
    }

    override fun takeFluid(predicate: Predicate<FluidStack>, limit: Long): FluidStack {
        var realLimit = limit
        var forgeStack = ForgeFluidStack.EMPTY
        for (i in 0 until handler.tanks) {
            val storedFluid = handler.getFluidInTank(i)
            if (predicate.test(storedFluid.toVanilla()) && (forgeStack.isEmpty || storedFluid.isFluidEqual(forgeStack))) {
                val extractedStack = handler.drain(storedFluid.copyWithCount(minOf(storedFluid.amount.toLong(), realLimit).toInt()), IFluidHandler.FluidAction.EXECUTE)
                if (!extractedStack.isEmpty) {
                    if (!forgeStack.isEmpty) {
                        forgeStack.amount += extractedStack.amount
                    } else {
                        forgeStack = extractedStack
                    }
                    realLimit -= extractedStack.amount
                    if (realLimit <= 0) {
                        return forgeStack.toVanilla()
                    }
                }
            }
        }
        return forgeStack.toVanilla()
    }

    override fun storeFluid(stack: FluidStack): FluidStack {
        val forgeStack = stack.toForge()
        for (i in 0 until handler.tanks) {
            if (handler.isFluidValid(i, forgeStack)) {
                val filled = handler.fill(forgeStack, IFluidHandler.FluidAction.EXECUTE)
                forgeStack.shrink(filled)
                if (forgeStack.isEmpty) {
                    return FluidStack.EMPTY
                }
            }
        }
        return forgeStack.toVanilla()
    }

    override fun setChanged() {
    }
}
