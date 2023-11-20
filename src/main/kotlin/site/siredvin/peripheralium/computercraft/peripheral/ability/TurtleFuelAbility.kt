package site.siredvin.peripheralium.computercraft.peripheral.ability

import dan200.computercraft.ComputerCraft
import site.siredvin.peripheralium.computercraft.peripheral.owner.TurtlePeripheralOwner

open class TurtleFuelAbility(owner: TurtlePeripheralOwner, override val maxFuelConsumptionRate: Int) :
    FuelAbility<TurtlePeripheralOwner>(owner) {

    override fun _consumeFuel(count: Int): Boolean {
        return owner.turtle.consumeFuel(count)
    }

    override val isFuelConsumptionDisable: Boolean
        get() = !ComputerCraft.turtlesNeedFuel
    override val fuelCount: Int
        get() = owner.turtle.fuelLevel
    override val fuelMaxCount: Int
        get() = owner.turtle.fuelLimit

    override fun addFuel(count: Int) {
        owner.turtle.addFuel(count)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is TurtleFuelAbility) return false
        return owner == other.owner && maxFuelConsumptionRate == other.maxFuelConsumptionRate
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + maxFuelConsumptionRate
        return result
    }
}
