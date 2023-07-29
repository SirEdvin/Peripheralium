package site.siredvin.peripheralium.computercraft.peripheral.ability

import site.siredvin.peripheralium.computercraft.peripheral.owner.TurtlePeripheralOwner

open class TurtleFuelAbility(owner: TurtlePeripheralOwner, override val maxFuelConsumptionRate: Int) :
    FuelAbility<TurtlePeripheralOwner>(owner) {

    override fun _consumeFuel(count: Int): Boolean {
        return owner.turtle.consumeFuel(count)
    }

    override val isFuelConsumptionDisable: Boolean
        get() = !owner.turtle.isFuelNeeded
    override val fuelCount: Int
        get() = owner.turtle.fuelLevel
    override val fuelMaxCount: Int
        get() = owner.turtle.fuelLimit

    override fun addFuel(count: Int) {
        owner.turtle.addFuel(count)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TurtleFuelAbility) return false
        if (!super.equals(other)) return false

        if (maxFuelConsumptionRate != other.maxFuelConsumptionRate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + maxFuelConsumptionRate
        return result
    }
}
