package site.siredvin.peripheralium.computercraft.peripheral.ability

import site.siredvin.peripheralium.api.peripheral.IOwnerAbility
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwnerAbility

class PeripheralOwnerAbility<T : IOwnerAbility> : IPeripheralOwnerAbility<T> {
    companion object {
        val FUEL: PeripheralOwnerAbility<FuelAbility<*>> = PeripheralOwnerAbility()
        val OPERATION: PeripheralOwnerAbility<OperationAbility> = PeripheralOwnerAbility()
        val EXPERIENCE: PeripheralOwnerAbility<ExperienceAbility> = PeripheralOwnerAbility()
        val SCANNING: PeripheralOwnerAbility<ScanningAbility<*>> = PeripheralOwnerAbility()
    }
}
