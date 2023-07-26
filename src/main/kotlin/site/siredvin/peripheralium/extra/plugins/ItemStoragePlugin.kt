package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.shared.peripheral.generic.data.ItemData
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.common.ExtractorProxy
import site.siredvin.peripheralium.common.configuration.PeripheraliumConfig
import java.util.*
import java.util.function.Predicate
import kotlin.math.min

class ItemStoragePlugin(private val level: Level, private val storage: Storage<ItemVariant>): IPeripheralPlugin {
    companion object {
        const val PLUGIN_TYPE = "item_storage"
    }

    override val additionalType: String
        get() = PLUGIN_TYPE

    @LuaFunction(mainThread = true)
    fun items(): List<Map<String, *>> {
        val result: MutableList<Map<String, *>> = mutableListOf()
        val transaction = Transaction.openOuter()
        transaction.use {
            storage.iterator(transaction).forEach {
                if (!it.isResourceBlank)
                    result.add(ItemData.fill(HashMap(), it.resource.toStack(it.amount.toInt())))
            }
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun pushItem(computer: IComputerAccess, toName: String, itemName: Optional<String>, limit: Optional<Long>): Long {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = ExtractorProxy.extractItemStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an fluid inventory")

        val predicate: Predicate<ItemVariant> = if (itemName.isEmpty) {
            Predicate { true }
        } else {
            val item = Registry.ITEM.get(ResourceLocation(itemName.get()))
            if (item == Items.AIR)
                throw LuaException("There is no item ${itemName.get()}")
            Predicate { it.isOf(item) }
        }
        val realLimit = min(PeripheraliumConfig.itemStorageTransferLimit, limit.orElse(Long.MAX_VALUE))
        return StorageUtil.move(storage, toStorage, predicate, realLimit, null)
    }

    @LuaFunction(mainThread = true)
    fun pullItem(computer: IComputerAccess, fromName: String, itemName: Optional<String>, limit: Optional<Long>): Long {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = ExtractorProxy.extractItemStorage(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an fluid inventory")

        val predicate: Predicate<ItemVariant> = if (itemName.isEmpty) {
            Predicate { true }
        } else {
            val item = Registry.ITEM.get(ResourceLocation(itemName.get()))
            if (item == Items.AIR)
                throw LuaException("There is no item ${itemName.get()}")
            Predicate { it.isOf(item) }
        }
        val realLimit = min(PeripheraliumConfig.itemStorageTransferLimit, limit.orElse(Long.MAX_VALUE))
        return StorageUtil.move(fromStorage, storage, predicate, realLimit, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemStoragePlugin) return false

        return storage == other.storage
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }
}