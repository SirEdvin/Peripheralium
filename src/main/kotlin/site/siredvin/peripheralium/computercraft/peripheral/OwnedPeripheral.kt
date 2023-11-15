package site.siredvin.peripheralium.computercraft.peripheral

import dan200.computercraft.api.lua.*
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.*
import site.siredvin.peripheralium.computercraft.peripheral.ability.OperationAbility
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility
import site.siredvin.peripheralium.util.OrientationUtil
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

abstract class OwnedPeripheral<O : IPeripheralOwner>(peripheralType: String, final override val peripheralOwner: O) :
    PluggablePeripheral<Any?>(peripheralType, peripheralOwner.targetRepresentation), IOwnedPeripheral<O>, IDynamicPeripheral {

    open val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data: MutableMap<String, Any> = HashMap()
            peripheralOwner.abilities.forEach(Consumer { ability: IOwnerAbility -> ability.collectConfiguration(data) })
            return data
        }

    @get:LuaFunction
    val configuration: Map<String, Any>
        get() = peripheralConfiguration
    protected val pos: BlockPos
        get() = peripheralOwner.pos
    protected val level: Level?
        get() = peripheralOwner.level

    override fun addOperations(operations: Array<IPeripheralOperation<*>>) {
        if (operations.isNotEmpty()) {
            val operationAbility = peripheralOwner.getAbility(PeripheralOwnerAbility.OPERATION)
                ?: throw IllegalArgumentException("This is not possible to attach plugin with operations to not operationable owner")
            for (operation in operations) operationAbility.registerOperation(operation)
        }
    }

    override fun collectPluginMethods() {
        super.collectPluginMethods()
        peripheralOwner.abilities.forEach {
            if (it is IPeripheralPlugin) {
                connectPlugin(it)
            }
        }
    }

    @Throws(LuaException::class)
    protected fun validateSide(direction: String): Direction {
        val dir = direction.uppercase()
        return OrientationUtil.getDirection(peripheralOwner.facing, dir)
    }

    @Throws(LuaException::class)
    protected fun <T> withOperation(
        operation: IPeripheralOperation<T>,
        context: T,
        check: IPeripheralCheck<T>?,
        method: IPeripheralFunction<T, MethodResult>,
        successCallback: Consumer<T>?,
    ): MethodResult {
        return withOperation(operation, context, check, method, successCallback, null)
    }

    @Throws(LuaException::class)
    protected fun <T> withOperation(
        operation: IPeripheralOperation<T>,
        context: T,
        check: IPeripheralCheck<T>?,
        method: IPeripheralFunction<T, MethodResult>,
        successCallback: Consumer<T>?,
        failCallback: BiConsumer<MethodResult?, OperationAbility.FailReason?>?,
    ): MethodResult {
        val operationAbility = peripheralOwner.getAbility(PeripheralOwnerAbility.OPERATION)
            ?: throw IllegalArgumentException("This shouldn't happen at all")
        return operationAbility.performOperation(operation, context, check, method, successCallback, failCallback)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherPluggable = other as? OwnedPeripheral<*> ?: return false
        if (peripheralTarget != otherPluggable.peripheralTarget || peripheralType != otherPluggable.peripheralType || peripheralOwner != other.peripheralOwner) return false
        if (!otherPluggable.initialized) otherPluggable.buildPlugins()
        return pluggedMethods.all {
            otherPluggable.pluggedMethods.any(it::equalWithoutTarget)
        }
    }
}
