package site.siredvin.peripheralium.storage

import site.siredvin.peripheralium.storages.energy.Energies
import site.siredvin.peripheralium.storages.energy.EnergyStack
import site.siredvin.peripheralium.storages.energy.EnergyStorage
import java.util.function.Predicate

class DummyEnergyStorage(override val capacity: Long, initialEnergy: EnergyStack) : EnergyStorage {
    private var internalEnergy: EnergyStack = initialEnergy

    override val energy: EnergyStack
        get() = internalEnergy

    override fun takeEnergy(predicate: Predicate<EnergyStack>, limit: Long): EnergyStack {
        if (predicate.test(internalEnergy)) return internalEnergy.split(limit)
        return EnergyStack.EMPTY
    }

    override fun storeEnergy(stack: EnergyStack): EnergyStack {
        if (stack.unit != internalEnergy.unit && internalEnergy.unit != Energies.EMPTY) return stack
        val possibleInjection = minOf(capacity - internalEnergy.amount, stack.amount)
        if (possibleInjection == 0L) return stack
        if (internalEnergy.unit == Energies.EMPTY) {
            internalEnergy = EnergyStack(stack.unit, possibleInjection)
        } else {
            internalEnergy.grow(possibleInjection)
        }
        stack.shrink(possibleInjection)
        return stack
    }

    override fun setChanged() {
    }
}
