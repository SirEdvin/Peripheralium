package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.detail.VanillaDetailRegistries
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.api.storage.TargetableSlottedStorage
import site.siredvin.peripheralium.util.assertBetween
import java.util.*

abstract class AbstractInventoryPlugin: IPeripheralPlugin {
    abstract val storage: SlottedStorage
    abstract val level: Level

    @LuaFunction(mainThread = true)
    fun size(): Int {
        return storage.size
    }

    @LuaFunction(mainThread = true)
    fun list(): Map<Int, Map<String, *>> {
        val result: MutableMap<Int, Map<String, *>> = hashMapOf()
        val size = storage.size
        for (i in 0 until size) {
            val stack = storage.getItem(i)
            if (!stack.isEmpty) result[i + 1] = VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack)
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun getItemDetail(slot: Int): Map<String, *>? {
        assertBetween(slot, 1, storage.size, "slot")
        val stack = storage.getItem(slot - 1)
        return if (stack.isEmpty) null else VanillaDetailRegistries.ITEM_STACK.getDetails(stack)
    }

    @LuaFunction(mainThread = true)
    fun getItemLimit(slot: Int): Int {
        assertBetween(slot, 1, storage.size, "slot")
        return storage.getItem(slot - 1).maxStackSize
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pushItems(computer: IComputerAccess, toName: String, fromSlot: Int, limit: Optional<Int>, toSlot: Optional<Int>): Int {

        // Find location to transfer to
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = ExtractorProxy.extractTargetableStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an inventory")

        // Validate slots

        // Validate slots
        val actualLimit: Int = limit.orElse(Int.MAX_VALUE)
        assertBetween(fromSlot, 1, storage.size, "fromtSlot")
        if (toSlot.isPresent) {
            if (toStorage !is TargetableSlottedStorage)
                throw LuaException("Target '$toName' is not slotted storage, so you can't provide slot")
            assertBetween(toSlot.get(), 1, toStorage.size, "toSlot")
        }

        return if (actualLimit <= 0) 0 else StorageUtils.moveBetweenStorages(
            storage,
            toStorage,
            actualLimit,
            fromSlot - 1,
            toSlot.orElse(0) - 1,
        )
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pullItems(computer: IComputerAccess, fromName: String, fromSlot: Int, limit: Optional<Int>, toSlot: Optional<Int>): Int {
        // Find location to transfer to
        val location =
            computer.getAvailablePeripheral(fromName) ?: throw LuaException("Source '$fromName' does not exist")
        val fromStorage = ExtractorProxy.extractTargetableStorage(level, location.target)
            ?: throw LuaException("Source '$fromName' is not an inventory")

        if (fromStorage !is SlottedStorage)
            throw LuaException("Source '$fromName' is not slotted storage")

        // Validate slots
        val actualLimit = limit.orElse(Int.MAX_VALUE)
        assertBetween(fromSlot, 1, fromStorage.size, "fromSlot")
        if (toSlot.isPresent)
            assertBetween(toSlot.get(),1, storage.size, "toSlot")
        return if (actualLimit <= 0) 0 else StorageUtils.moveBetweenStorages(
            fromStorage,
            storage,
            actualLimit,
            fromSlot - 1,
            toSlot.orElse(0) - 1,
        )
    }
}