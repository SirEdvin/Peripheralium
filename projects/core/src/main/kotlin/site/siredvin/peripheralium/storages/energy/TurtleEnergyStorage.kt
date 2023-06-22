package site.siredvin.peripheralium.storages.energy

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleAnimation
import java.util.function.Predicate

class TurtleEnergyStorage(private val turtle: ITurtleAccess) : EnergyStorage {
    override val energy: EnergyStack
        get() = EnergyStack(Energies.TURTLE_FUEL, turtle.fuelLevel.toLong())

    override val capacity: Long
        get() = turtle.fuelLimit.toLong()

    override fun takeEnergy(predicate: Predicate<EnergyStack>, limit: Long): EnergyStack {
        if (!predicate.test(energy)) return EnergyStack.EMPTY
        val extractedEnergy = minOf(limit, turtle.fuelLevel.toLong())
        turtle.addFuel(-extractedEnergy.toInt())
        return EnergyStack(Energies.TURTLE_FUEL, extractedEnergy)
    }

    override fun storeEnergy(stack: EnergyStack): EnergyStack {
        if (!stack.`is`(Energies.TURTLE_FUEL)) return stack
        val insertedEnergy = minOf(stack.amount, turtle.fuelLimit.toLong() - turtle.fuelLevel.toLong())
        turtle.addFuel(insertedEnergy.toInt())
        stack.shrink(insertedEnergy)
        if (stack.amount == 0L) return EnergyStack.EMPTY
        return stack
    }

    override fun setChanged() {
        turtle.playAnimation(TurtleAnimation.NONE)
    }
}
