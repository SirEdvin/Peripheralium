package site.siredvin.peripheralium.storages.fluid

import java.util.function.Predicate

interface FluidStorage : FluidSink {
    fun getFluids(): Iterator<FluidStack>
    fun takeFluid(predicate: Predicate<FluidStack>, limit: Long): FluidStack

    fun moveTo(to: FluidSink, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return FluidStorageUtils.naiveMove(this, to, limit, takePredicate)
        }
        return to.moveFrom(this, limit, takePredicate)
    }
}
