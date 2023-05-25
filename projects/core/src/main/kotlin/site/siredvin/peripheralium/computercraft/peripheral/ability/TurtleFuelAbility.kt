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
}
