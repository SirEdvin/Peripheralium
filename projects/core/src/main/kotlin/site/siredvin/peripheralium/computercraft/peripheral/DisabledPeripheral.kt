package site.siredvin.peripheralium.computercraft.peripheral

import site.siredvin.peripheralium.computercraft.peripheral.owner.DisabledPeripheralOwner

object DisabledPeripheral : OwnedPeripheral<DisabledPeripheralOwner>("disabled", DisabledPeripheralOwner()) {
    override val isEnabled: Boolean
        get() = true

    override fun equals(other: Any?): Boolean {
        return other === this
    }
}
