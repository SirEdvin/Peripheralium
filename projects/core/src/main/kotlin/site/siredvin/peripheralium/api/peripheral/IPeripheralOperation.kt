package site.siredvin.peripheralium.api.peripheral

import site.siredvin.peripheralium.api.config.IConfigHandler

interface IPeripheralOperation<T> : IConfigHandler {
    fun getCooldown(context: T): Int
    fun getCost(context: T): Int
    fun computerDescription(): Map<String, Any?>
}
