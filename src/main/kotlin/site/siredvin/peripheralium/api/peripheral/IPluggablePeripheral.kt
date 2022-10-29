package site.siredvin.peripheralium.api.peripheral

import dan200.computercraft.api.peripheral.IComputerAccess

interface IPluggablePeripheral {
    val connectedComputers: List<IComputerAccess>
}