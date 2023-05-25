package site.siredvin.peripheralium.computercraft.peripheral

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import kotlinx.atomicfu.locks.withLock
import site.siredvin.peripheralium.api.peripheral.IObservingPeripheralPlugin
import site.siredvin.peripheralium.api.peripheral.IPeripheralOperation
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.api.peripheral.IPluggablePeripheral
import site.siredvin.peripheralium.extra.plugins.PeripheralPluginUtils
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer

open class PluggablePeripheral<T>(private val peripheralType: String, private val peripheralTarget: T?) : IDynamicPeripheral, IPluggablePeripheral {
    protected val _connectedComputers: MutableList<IComputerAccess> = ArrayList()
    protected var initialized = false
    protected val pluggedMethods: MutableList<BoundMethod> = ArrayList()
    protected var plugins: MutableList<IPeripheralPlugin>? = null
    protected var _methodNames = Array(0) { "" }
    protected var additionalTypeStorage: MutableSet<String>? = null
    protected var connectedComputersLock: ReentrantLock = ReentrantLock()

    val connectedComputers: List<IComputerAccess>
        get() = _connectedComputers

    protected fun initAdditionalTypeStorage() {
        additionalTypeStorage = mutableSetOf()
        additionalTypeStorage!!.add(PeripheralPluginUtils.Type.PLUGGABLE)
    }

    protected open fun addAdditionalType(additionalType: String?) {
        if (additionalType != null && additionalType != peripheralType) {
            if (additionalTypeStorage == null) {
                initAdditionalTypeStorage()
            }
            additionalTypeStorage!!.add(additionalType)
        }
    }

    protected open fun connectPlugin(plugin: IPeripheralPlugin) {
        pluggedMethods.addAll(plugin.methods)
        addAdditionalType(plugin.additionalType)
        plugin.connectedPeripheral = this
    }

    protected open fun collectPluginMethods() {
        if (plugins != null) plugins!!.forEach(Consumer { connectPlugin(it) })
    }

    protected open fun buildPlugins() {
        if (!initialized) {
            initialized = true
            pluggedMethods.clear()
            if (additionalTypeStorage == null) {
                initAdditionalTypeStorage()
            } else {
                additionalTypeStorage?.clear()
            }
            collectPluginMethods()
            _methodNames = pluggedMethods.stream().map { obj: BoundMethod -> obj.name }.toArray { size -> Array(size) { "" } }
        }
    }

    protected open fun addOperations(operations: List<IPeripheralOperation<*>>) {
        require(operations.isEmpty()) { "This is not possible to attach plugin with operations to not operationable owner" }
    }

    fun addPlugin(plugin: IPeripheralPlugin) {
        if (plugins == null) {
            plugins = LinkedList()
        }
        plugins!!.add(plugin)
        addOperations(plugin.operations)
        addAdditionalType(plugin.additionalType)
    }

    override fun attach(computer: IComputerAccess) {
        connectedComputersLock.withLock {
            _connectedComputers.add(computer)
            if (_connectedComputers.size == 1 && plugins != null) {
                plugins!!.forEach {
                    if (it is IObservingPeripheralPlugin) {
                        it.onFirstAttach()
                    }
                }
            }
        }
    }

    override fun detach(computer: IComputerAccess) {
        connectedComputersLock.withLock {
            _connectedComputers.remove(computer)
            if (_connectedComputers.isEmpty() && plugins != null) {
                plugins!!.forEach {
                    if (it is IObservingPeripheralPlugin) {
                        it.onLastDetach()
                    }
                }
            }
        }
    }

    override fun forEachComputer(func: Consumer<IComputerAccess>) {
        connectedComputersLock.withLock {
            _connectedComputers.forEach { func.accept(it) }
        }
    }

    override fun isComputerPresent(computerID: Int): Boolean {
        connectedComputersLock.withLock {
            return _connectedComputers.any { it.id == computerID }
        }
    }

    override val connectedComputersCount: Int
        get() = connectedComputersLock.withLock { return _connectedComputers.size }

    override fun equals(iPeripheral: IPeripheral?): Boolean {
        return iPeripheral === this
    }

    override fun getMethodNames(): Array<String> {
        if (!initialized) {
            buildPlugins()
        }
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
        arguments: IArguments,
    ): MethodResult {
        if (!initialized) {
            buildPlugins()
        }
        return pluggedMethods[index].apply(access, context, arguments)
    }
}
