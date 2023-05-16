package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.detail.VanillaDetailRegistries
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.api.storage.Storage
import site.siredvin.peripheralium.util.representation.LuaRepresentation
import java.util.*
import java.util.function.Predicate
import kotlin.math.min

abstract class AbstractItemStoragePlugin: IPeripheralPlugin {
    abstract val storage: Storage
    abstract val level: Level
    abstract val itemStorageTransferLimit: Int

    override val additionalType: String
        get() = PeripheralPluginUtils.TYPES.ITEM_STORAGE

    open fun itemsImpl(): List<MutableMap<String, *>> {
        val result: MutableList<MutableMap<String, *>> = mutableListOf()
        storage.getItems().forEach {
            if (!it.isEmpty)
                result.add(LuaRepresentation.forItemStack(it))
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun items(): List<Map<String, *>> {
        return itemsImpl()
    }

    @LuaFunction(mainThread = true)
    fun pushItem(computer: IComputerAccess, toName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = ExtractorProxy.extractTargetableStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an targetable storage")

        val predicate: Predicate<ItemStack> = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        val realLimit = min(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, takePredicate = predicate)
    }

    @LuaFunction(mainThread = true)
    fun pullItem(computer: IComputerAccess, fromName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = ExtractorProxy.extractStorage(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an storage")

        val predicate: Predicate<ItemStack> = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        val realLimit = min(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, takePredicate = predicate)
    }
}