package site.siredvin.peripheralium.computercraft.peripheral

import dan200.computercraft.api.lua.*
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import kotlinx.atomicfu.locks.withLock
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.*
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility
import site.siredvin.peripheralium.ext.xor
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer

abstract class OwnedPeripheral<O : IPeripheralOwner>(protected open val peripheralType: String, final override val peripheralOwner: O) :
    IOwnedPeripheral<O>,
    IDynamicPeripheral,
    IExpandedPeripheral {
    protected open val internalConnectedComputers: MutableList<IComputerAccess> = mutableListOf()
    protected open var initialized = false
    protected open val pluggedMethods: MutableList<BoundMethod> = mutableListOf()
    protected open var plugins: MutableList<IPeripheralPlugin> = mutableListOf()
    protected open var internalMethodNames = Array(0) { "" }
    protected open var connectedComputersLock: ReentrantLock = ReentrantLock()

    @get:LuaFunction
    val configuration: Map<String, Any>
        get() = peripheralConfiguration

    @Deprecated(message = "Use peripheral owner directly, please", replaceWith = ReplaceWith("peripheralOwner.level"))
    val level: Level?
        get() = peripheralOwner.level

    @Deprecated(message = "Use peripheral owner directly, please", replaceWith = ReplaceWith("peripheralOwner.pos"))
    val pos: BlockPos
        get() = peripheralOwner.pos

    override val connectedComputers: List<IComputerAccess>
        get() = internalConnectedComputers

    protected open val additionalTypeStorage: MutableSet<String> by lazy {
        mutableSetOf()
    }

    override val connectedComputersCount: Int
        get() = connectedComputersLock.withLock { return internalConnectedComputers.size }

    open val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data: MutableMap<String, Any> = HashMap()
            peripheralOwner.abilities.forEach(Consumer { ability: IOwnerAbility -> ability.collectConfiguration(data) })
            return data
        }

    protected open fun addOperations(operations: List<IPeripheralOperation<*, *>>) {
        if (operations.isNotEmpty()) {
            val operationAbility = peripheralOwner.getAbility(PeripheralOwnerAbility.OPERATION)
            if (operationAbility != null) {
                for (operation in operations) operationAbility.registerOperation(operation)
            }
        }
    }

    protected open fun collectPlugin(server: MinecraftServer, plugin: IPeripheralPlugin) {
        pluggedMethods.addAll(plugin.getMethods(server))
        if (plugin.additionalType != null) addAdditionalType(plugin.additionalType!!)
        plugin.connectedPeripheral = this
        addOperations(plugin.operations)
    }

    protected open fun collectPluginMethods(server: MinecraftServer) {
        plugins.forEach(Consumer { collectPlugin(server, it) })
        peripheralOwner.abilities.forEach {
            if (it is IPeripheralPlugin) {
                collectPlugin(server, it)
            }
        }
    }

    open fun addAdditionalType(additionalType: String) {
        if (additionalType != peripheralType) additionalTypeStorage.add(additionalType)
    }

    protected open fun buildPlugins() {
        if (!initialized && PeripheraliumPlatform.minecraftServer != null) {
            initialized = true
            pluggedMethods.clear()
            additionalTypeStorage.clear()
            collectPluginMethods(PeripheraliumPlatform.minecraftServer!!)
            internalMethodNames = pluggedMethods.stream().map { obj: BoundMethod -> obj.name }.toArray { size -> Array(size) { "" } }
        }
    }

    fun addPlugin(plugin: IPeripheralPlugin) {
        plugins.add(plugin)
        if (plugin.additionalType != null) {
            addAdditionalType(plugin.additionalType!!)
        }
    }

    override fun attach(computer: IComputerAccess) {
        connectedComputersLock.withLock {
            internalConnectedComputers.add(computer)
            if (internalConnectedComputers.size == 1) {
                plugins.forEach {
                    if (it is IObservingPeripheralPlugin) {
                        it.onFirstAttach()
                    }
                }
            }
        }
    }

    override fun detach(computer: IComputerAccess) {
        connectedComputersLock.withLock {
            internalConnectedComputers.remove(computer)
            if (internalConnectedComputers.isEmpty()) {
                plugins.forEach {
                    if (it is IObservingPeripheralPlugin) {
                        it.onLastDetach()
                    }
                }
            }
        }
    }

    override fun forEachComputer(func: Consumer<IComputerAccess>) {
        connectedComputersLock.withLock {
            internalConnectedComputers.forEach { func.accept(it) }
        }
    }

    override fun isComputerPresent(computerID: Int): Boolean {
        connectedComputersLock.withLock {
            return internalConnectedComputers.any { it.id == computerID }
        }
    }

    override fun getMethodNames(): Array<String> {
        if (!initialized) {
            buildPlugins()
        }
        return internalMethodNames
    }

    override fun getAdditionalTypes(): Set<String> = additionalTypeStorage

    override fun getType(): String = peripheralType

    override fun getTarget(): Any? = peripheralOwner.targetRepresentation

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

    protected open fun internalEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OwnedPeripheral<*>) return false

        if (peripheralType != other.peripheralType) return false
        if (peripheralOwner != other.peripheralOwner) return false
        if (plugins.map { it.javaClass }.toSet().xor(other.plugins.map { it.javaClass }.toSet()).isNotEmpty()) return false

        return true
    }

    override fun equals(other: IPeripheral?): Boolean = internalEquals(other)

    override fun equals(other: Any?): Boolean = internalEquals(other)

    override fun hashCode(): Int {
        var result = peripheralType.hashCode()
        result = 31 * result + peripheralOwner.hashCode()
        result = 31 * result + plugins.hashCode()
        return result
    }
}
