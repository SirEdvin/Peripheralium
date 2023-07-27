package site.siredvin.peripheralium.computercraft.peripheral

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.core.methods.PeripheralMethod
import java.util.*

class BoundMethod(private val target: Any, val name: String, private val method: PeripheralMethod) {

    @Throws(LuaException::class)
    fun apply(
        access: IComputerAccess,
        context: ILuaContext,
        arguments: IArguments,
    ): MethodResult {
        return method.apply(target, context, access, arguments)
    }

    fun equalWithoutTarget(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoundMethod) return false
        return name == other.name && method == other.method
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoundMethod) return false
        return target == other.target && name == other.name && method == other.method
    }

    override fun hashCode(): Int {
        return Objects.hash(target, name, method)
    }
}
