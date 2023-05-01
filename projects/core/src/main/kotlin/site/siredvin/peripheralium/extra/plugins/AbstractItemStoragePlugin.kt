package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.detail.VanillaDetailRegistries
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.api.storage.Storage
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.common.configuration.PeripheraliumConfig
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.*
import java.util.function.Predicate
import kotlin.math.min

abstract class AbstractItemStoragePlugin: IPeripheralPlugin {
    abstract val storage: Storage
    abstract val level: Level

    @LuaFunction(mainThread = true)
    fun items(): List<Map<String, *>> {
        val result: MutableList<Map<String, *>> = mutableListOf()
        storage.getItems().forEach {
            if (!it.isEmpty)
                result.add(VanillaDetailRegistries.ITEM_STACK.getDetails(it))
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun pushItem(computer: IComputerAccess, toName: String, itemName: Optional<String>, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = ExtractorProxy.extractTargetableStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an targetable storage")

        val predicate: Predicate<ItemStack> = if (itemName.isEmpty) {
            Predicate { true }
        } else {
            val item = XplatRegistries.ITEMS.get(ResourceLocation(itemName.get()))
            if (item == Items.AIR)
                throw LuaException("There is no item ${itemName.get()}")
            Predicate { it.`is`(item) }
        }
        val realLimit = min(PeripheraliumConfig.itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return StorageUtils.moveBetweenStorages(storage, toStorage, realLimit, takePredicate = predicate)
    }

    @LuaFunction(mainThread = true)
    fun pullItem(computer: IComputerAccess, fromName: String, itemName: Optional<String>, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = ExtractorProxy.extractStorage(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an storage")

        val predicate: Predicate<ItemStack> = if (itemName.isEmpty) {
            Predicate { true }
        } else {
            val item = XplatRegistries.ITEMS.get(ResourceLocation(itemName.get()))
            if (item == Items.AIR)
                throw LuaException("There is no item ${itemName.get()}")
            Predicate { it.`is`(item) }
        }
        val realLimit = min(PeripheraliumConfig.itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return StorageUtils.moveBetweenStorages(fromStorage, storage, realLimit, takePredicate = predicate)
    }
}