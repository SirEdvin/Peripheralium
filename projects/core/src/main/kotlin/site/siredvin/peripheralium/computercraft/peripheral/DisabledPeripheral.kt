package site.siredvin.peripheralium.computercraft.peripheral

import site.siredvin.peripheralium.computercraft.peripheral.owner.PocketPeripheralOwner

object DisabledPeripheral: OwnedPeripheral<PocketPeripheralOwner>("disabled", PocketPeripheralOwner(null)) {
    override val isEnabled: Boolean
        get() = true
}