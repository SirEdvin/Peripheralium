package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.detail.VanillaDetailRegistries
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.common.FabricExtractorProxy
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
            storage.iterator().forEach {
                if (!it.isResourceBlank)
                    result.add(VanillaDetailRegistries.ITEM_STACK.getDetails(it.resource.toStack(it.amount.toInt())))
            }
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun pushItem(computer: IComputerAccess, toName: String, itemName: Optional<String>, limit: Optional<Long>): Long {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = FabricExtractorProxy.extractItemStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an fluid inventory")

        val predicate: Predicate<ItemVariant> = if (itemName.isEmpty) {
            Predicate { true }
        } else {
            val item = BuiltInRegistries.ITEM.get(ResourceLocation(itemName.get()))
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

        val fromStorage = FabricExtractorProxy.extractItemStorage(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an fluid inventory")

        val predicate: Predicate<ItemVariant> = if (itemName.isEmpty) {
            Predicate { true }
        } else {
            val item = BuiltInRegistries.ITEM.get(ResourceLocation(itemName.get()))
            if (item == Items.AIR)
                throw LuaException("There is no item ${itemName.get()}")
            Predicate { it.isOf(item) }
        }
        val realLimit = min(PeripheraliumConfig.itemStorageTransferLimit, limit.orElse(Long.MAX_VALUE))
        return StorageUtil.move(fromStorage, storage, predicate, realLimit, null)
    }
}