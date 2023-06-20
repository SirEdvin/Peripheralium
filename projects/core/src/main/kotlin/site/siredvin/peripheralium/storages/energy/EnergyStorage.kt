package site.siredvin.peripheralium.storages.energy

import java.util.function.Predicate

interface EnergyStorage : EnergySink {
    val energy: EnergyStack
    val capacity: Long
    fun takeEnergy(predicate: Predicate<EnergyStack>, limit: Int): EnergyStack

    fun moveTo(to: EnergySink, limit: Int, takePredicate: Predicate<EnergyStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return EnergyStorageUtils.naiveMove(this, to, limit, takePredicate)
        }
        return to.moveFrom(this, limit, takePredicate)
    }
}
