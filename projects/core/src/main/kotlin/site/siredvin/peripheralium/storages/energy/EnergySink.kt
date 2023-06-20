package site.siredvin.peripheralium.storages.energy

import java.util.function.Predicate

interface EnergySink {
    fun moveFrom(from: EnergyStorage, limit: Int, takePredicate: Predicate<EnergyStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return EnergyStorageUtils.naiveMove(from, this, limit, takePredicate)
        }
        return from.moveTo(this, limit, takePredicate)
    }
    fun storeEnergy(stack: EnergyStack): EnergyStack
    fun setChanged()

    val movableType: String?
        get() = null
}
