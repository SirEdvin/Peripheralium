package site.siredvin.peripheralium.api.peripheral

import dan200.computercraft.core.asm.NamedMethod
import dan200.computercraft.core.asm.PeripheralMethod
import net.minecraft.server.MinecraftServer
import site.siredvin.peripheralium.computercraft.peripheral.BoundMethod
import java.util.stream.Collectors

interface IPeripheralPlugin {
    var connectedPeripheral: IExpandedPeripheral?
        get() = null
        set(@Suppress("UNUSED_PARAMETER") value) {}

    fun getMethods(server: MinecraftServer): List<BoundMethod> = PeripheralMethod.GENERATOR.getMethods(this.javaClass).stream()
        .map { named: NamedMethod<PeripheralMethod> -> BoundMethod(this, named.name, named.method) }
        .collect(Collectors.toList())

    val operations: List<IPeripheralOperation<*, *>>
        get() = emptyList()

    val additionalType: String?
        get() = null
}
