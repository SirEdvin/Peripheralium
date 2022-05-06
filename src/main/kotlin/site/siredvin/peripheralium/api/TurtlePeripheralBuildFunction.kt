package site.siredvin.peripheralium.api

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import site.siredvin.peripheralium.api.peripheral.IBasePeripheral

fun interface TurtlePeripheralBuildFunction<T : IBasePeripheral<*>> {
    fun build(turtle: ITurtleAccess, side: TurtleSide): T
}