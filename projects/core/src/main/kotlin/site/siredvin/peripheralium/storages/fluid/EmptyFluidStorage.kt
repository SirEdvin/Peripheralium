package site.siredvin.peripheralium.storages.fluid

import java.util.function.Predicate

object EmptyFluidStorage : FluidStorage {
    override fun getFluids(): Iterator<FluidStack> {
        return emptyList<FluidStack>().iterator()
    }

    override fun takeFluid(predicate: Predicate<FluidStack>, limit: Long): FluidStack {
        return FluidStack.EMPTY
    }

    override fun storeFluid(stack: FluidStack): FluidStack {
        return stack
    }

    override fun setChanged() {
    }
}
