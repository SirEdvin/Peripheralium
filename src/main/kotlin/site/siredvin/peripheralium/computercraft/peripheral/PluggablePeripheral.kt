package site.siredvin.peripheralium.computercraft.peripheral

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import site.siredvin.peripheralium.api.peripheral.IPeripheralOperation
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.api.peripheral.IPluggablePeripheral
import java.util.*
import java.util.function.Consumer

open class PluggablePeripheral<T>(private val peripheralType: String, private val peripheralTarget: T?): IDynamicPeripheral, IPluggablePeripheral {
    protected val _connectedComputers: MutableList<IComputerAccess> = ArrayList()
    protected var initialized = false
    protected val pluggedMethods: MutableList<BoundMethod> = ArrayList()
    protected var plugins: MutableList<IPeripheralPlugin>? = null
    protected var _methodNames = Array(0) { "" }
    protected var additionalTypeStorage: MutableSet<String>? = null

    override val connectedComputers: List<IComputerAccess>
        get() = _connectedComputers

    protected fun addAdditionalType(additionalType: String?) {
        if (additionalType != null && additionalType != peripheralType) {
            if (additionalTypeStorage == null)
                additionalTypeStorage = mutableSetOf()
            additionalTypeStorage!!.add(additionalType)
        }
    }

    protected fun buildPlugins() {
        if (!initialized) {
            initialized = true
            pluggedMethods.clear()
            if (additionalTypeStorage == null) {
                additionalTypeStorage = mutableSetOf()
            } else {
                additionalTypeStorage?.clear()
            }
            if (plugins != null) plugins!!.forEach(Consumer { plugin: IPeripheralPlugin ->
                pluggedMethods.addAll(plugin.methods)
                addAdditionalType(plugin.additionalType)
                plugin.connectedPeripheral = this
            })
            _methodNames = pluggedMethods.stream().map { obj: BoundMethod -> obj.name }.toArray { size -> Array(size) { "" } }
        }
    }

    protected open fun addOperations(operations: Array<IPeripheralOperation<*>>) {
        require(operations.isEmpty()) { "This is not possible to attach plugin with operations to not operationable owner" }
    }

    fun addPlugin(plugin: IPeripheralPlugin) {
        if (plugins == null)
            plugins = LinkedList()
        plugins!!.add(plugin)
        addOperations(plugin.operations)
        addAdditionalType(plugin.additionalType)
    }

    override fun attach(computer: IComputerAccess) {
        _connectedComputers.add(computer)
    }

    override fun detach(computer: IComputerAccess) {
        _connectedComputers.remove(computer)
    }

    override fun equals(iPeripheral: IPeripheral?): Boolean {
        return iPeripheral === this
    }

    override fun getMethodNames(): Array<String> {
        if (!initialized)
            buildPlugins()
        return _methodNames
    }

    override fun getAdditionalTypes(): Set<String> {
        return additionalTypeStorage ?: emptySet()
    }

    override fun getType(): String {
        return peripheralType
    }

    override fun getTarget(): Any? {
        return peripheralTarget
    }

    @Throws(LuaException::class)
    override fun callMethod(
        access: IComputerAccess,
        context: ILuaContext,
        index: Int,
        arguments: IArguments
    ): MethodResult {
        if (!initialized)
            buildPlugins()
        return pluggedMethods[index].apply(access, context, arguments)
    }
}