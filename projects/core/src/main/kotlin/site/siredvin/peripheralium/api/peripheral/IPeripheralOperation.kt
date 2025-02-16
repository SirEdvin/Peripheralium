package site.siredvin.peripheralium.api.peripheral

import site.siredvin.peripheralium.api.config.IConfigHandler

interface IPeripheralOperation<C, T> : IConfigHandler<C> {
    fun getCooldown(context: T): Int
    fun getCost(context: T): Int
    fun computerDescription(): Map<String, Any?>
}
