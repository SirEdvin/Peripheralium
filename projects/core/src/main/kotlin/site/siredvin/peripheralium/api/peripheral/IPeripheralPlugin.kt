package site.siredvin.peripheralium.api.peripheral

import dan200.computercraft.core.asm.NamedMethod
import dan200.computercraft.core.asm.PeripheralMethod
import site.siredvin.peripheralium.computercraft.peripheral.BoundMethod
import java.util.stream.Collectors

interface IPeripheralPlugin {
    var connectedPeripheral: IPluggablePeripheral?
        get() = null
        set(value) {}

    val methods: List<BoundMethod>
        get() = PeripheralMethod.GENERATOR.getMethods(this.javaClass).stream()
            .map { named: NamedMethod<PeripheralMethod> -> BoundMethod(this, named) }
            .collect(Collectors.toList())

    val operations: List<IPeripheralOperation<*>>
        get() = emptyList()

    val additionalType: String?
        get() = null
}
