package site.siredvin.peripheralium.storages.fluid

import java.util.function.Predicate

object EmptyFluidStorage : FluidStorage {
    override fun getFluids(): Iterator<FluidStack> = emptyList<FluidStack>().iterator()

    override fun takeFluid(predicate: Predicate<FluidStack>, limit: Long): FluidStack = FluidStack.EMPTY

    override fun storeFluid(stack: FluidStack): FluidStack = stack

    override fun setChanged() {
    }
}
