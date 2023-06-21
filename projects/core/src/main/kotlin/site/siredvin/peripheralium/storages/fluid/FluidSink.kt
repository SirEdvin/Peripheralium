package site.siredvin.peripheralium.storages.fluid

import java.util.function.Predicate

interface FluidSink {
    fun moveFrom(from: FluidStorage, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return FluidStorageUtils.naiveMove(from, this, limit, takePredicate)
        }
        return from.moveTo(this, limit, takePredicate)
    }
    fun storeFluid(stack: FluidStack): FluidStack
    fun setChanged()

    val movableType: String?
        get() = null
}
