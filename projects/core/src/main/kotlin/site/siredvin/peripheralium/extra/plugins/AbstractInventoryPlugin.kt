package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.storages.item.ItemStorageExtractor
import site.siredvin.peripheralium.storages.item.ItemStorageUtils
import site.siredvin.peripheralium.storages.item.SlottedItemSink
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import site.siredvin.peripheralium.util.assertBetween
import site.siredvin.peripheralium.util.representation.LuaRepresentation
import site.siredvin.peripheralium.util.representation.RepresentationMode
import java.util.*

abstract class AbstractInventoryPlugin : IPeripheralPlugin {
    abstract val storage: SlottedItemStorage
    abstract val level: Level

    override val additionalType: String
        get() = PeripheralPluginUtils.Type.INVENTORY

    open fun sizeImpl(): Int {
        return storage.size
    }

    open fun listImpl(): Map<Int, Map<String, *>> {
        val result: MutableMap<Int, Map<String, *>> = hashMapOf()
        val size = storage.size
        for (i in 0 until size) {
            val stack = storage.getItem(i)
            if (!stack.isEmpty) result[i + 1] = LuaRepresentation.forItemStack(stack, RepresentationMode.BASE)
        }
        return result
    }

    open fun getItemDetailImpl(slot: Int): Map<String, *>? {
        val stack = storage.getItem(slot)
        return if (stack.isEmpty) null else LuaRepresentation.forItemStack(stack)
    }

    open fun getItemLimitImpl(slot: Int): Int {
        return storage.getItem(slot).maxStackSize
    }

    @LuaFunction(mainThread = true)
    fun size(): Int {
        return sizeImpl()
    }

    @LuaFunction(mainThread = true)
    fun list(): Map<Int, Map<String, *>> {
        return listImpl()
    }

    @LuaFunction(mainThread = true)
    fun getItemDetail(slot: Int): Map<String, *>? {
        assertBetween(slot, 1, storage.size, "slot")
        return getItemDetailImpl(slot - 1)
    }

    @LuaFunction(mainThread = true)
    fun getItemLimit(slot: Int): Int {
        assertBetween(slot, 1, storage.size, "slot")
        return getItemLimitImpl(slot - 1)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pushItems(computer: IComputerAccess, toName: String, fromSlot: Int, limit: Optional<Int>, toSlot: Optional<Int>): Int {
        // Find location to transfer to
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = ItemStorageExtractor.extractItemSinkFromUnknown(level, location.target)
            ?: throw LuaException("Target '$toName' is not an inventory")

        // Validate slots

        // Validate slots
        val actualLimit: Int = limit.orElse(Int.MAX_VALUE)
        assertBetween(fromSlot, 1, storage.size, "fromtSlot")
        if (toSlot.isPresent) {
            if (toStorage !is SlottedItemSink) {
                throw LuaException("Target '$toName' is not slotted storage, so you can't provide slot")
            }
            assertBetween(toSlot.get(), 1, toStorage.size, "toSlot")
        }

        return if (actualLimit <= 0) 0 else storage.moveTo(toStorage, actualLimit, fromSlot - 1, toSlot.orElse(0) - 1, ItemStorageUtils.ALWAYS)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pullItems(computer: IComputerAccess, fromName: String, fromSlot: Int, limit: Optional<Int>, toSlot: Optional<Int>): Int {
        // Find location to transfer to
        val location =
            computer.getAvailablePeripheral(fromName) ?: throw LuaException("Source '$fromName' does not exist")
        val fromStorage = ItemStorageExtractor.extractItemSinkFromUnknown(level, location.target)
            ?: throw LuaException("Source '$fromName' is not an inventory")

        if (fromStorage !is SlottedItemStorage) {
            throw LuaException("Source '$fromName' is not slotted storage")
        }

        // Validate slots
        val actualLimit = limit.orElse(Int.MAX_VALUE)
        assertBetween(fromSlot, 1, fromStorage.size, "fromSlot")
        if (toSlot.isPresent) {
            assertBetween(toSlot.get(), 1, storage.size, "toSlot")
        }
        return if (actualLimit <= 0) 0 else storage.moveFrom(fromStorage, actualLimit, toSlot.orElse(0) - 1, fromSlot - 1, ItemStorageUtils.ALWAYS)
    }
}
