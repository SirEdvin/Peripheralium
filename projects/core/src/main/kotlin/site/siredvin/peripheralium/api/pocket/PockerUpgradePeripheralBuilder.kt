package site.siredvin.peripheralium.api.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral

fun interface PockerUpgradePeripheralBuilder<T : IOwnedPeripheral<*>> {
    fun build(access: IPocketAccess): T
}
