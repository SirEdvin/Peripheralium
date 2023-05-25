package site.siredvin.peripheralium.api.peripheral

import net.minecraft.core.Direction

interface IPeripheralProvider<T : IOwnedPeripheral<*>> {
    fun getPeripheral(side: Direction): T?
}
