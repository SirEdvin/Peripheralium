package site.siredvin.peripheralium.computercraft.peripheral.ability

import site.siredvin.peripheralium.api.peripheral.IOwnerAbility

class PeripheralOwnerAbility<T : IOwnerAbility> {
    companion object {
        val FUEL: PeripheralOwnerAbility<FuelAbility<*>> = PeripheralOwnerAbility()
        val OPERATION: PeripheralOwnerAbility<OperationAbility> = PeripheralOwnerAbility()
        val EXPERIENCE: PeripheralOwnerAbility<ExperienceAbility> = PeripheralOwnerAbility()
    }
}
