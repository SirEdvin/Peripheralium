package site.siredvin.peripheralium.api.peripheral

import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral

interface IOwnedPeripheral<T : IPeripheralOwner?> : IPeripheral {
    val isEnabled: Boolean
    val connectedComputers: List<IComputerAccess>
    val peripheralOwner: T
}
