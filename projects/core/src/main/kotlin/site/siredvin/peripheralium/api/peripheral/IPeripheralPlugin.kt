package site.siredvin.peripheralium.api.peripheral

import dan200.computercraft.shared.computer.core.ServerContext
import net.minecraft.server.MinecraftServer
import site.siredvin.peripheralium.computercraft.peripheral.BoundMethod

interface IPeripheralPlugin {
    var connectedPeripheral: IPluggablePeripheral?
        get() = null
        set(value) {}

    fun getMethods(server: MinecraftServer): List<BoundMethod> {
        return ServerContext.get(server).peripheralMethods().getSelfMethods(this).map {
            BoundMethod(this, it.key, it.value)
        }
    }

    val operations: List<IPeripheralOperation<*>>
        get() = emptyList()

    val additionalType: String?
        get() = null
}
