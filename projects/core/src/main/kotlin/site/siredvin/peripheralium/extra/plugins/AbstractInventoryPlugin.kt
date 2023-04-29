package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.detail.VanillaDetailRegistries
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.Container
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.common.ExtractorProxy
import site.siredvin.peripheralium.util.ContainerHelpers
import site.siredvin.peripheralium.util.assertBetween
import java.util.*

abstract class AbstractInventoryPlugin: IPeripheralPlugin {

    /*Kotlin rework from https://github.com/cc-tweaked/cc-restitched/blob/mc-1.18.x%2Fstable/src/main/java/dan200/computercraft/shared/peripheral/generic/methods/InventoryMethods.java */

    abstract val container: Container
    abstract val level: Level

    @LuaFunction(mainThread = true)
    fun size(): Int {
        return container.containerSize
    }

    @LuaFunction(mainThread = true)
    fun list(): Map<Int, Map<String, *>> {
        val result: MutableMap<Int, Map<String, *>> = hashMapOf()
        val size = container.containerSize
        for (i in 0 until size) {
            val stack = container.getItem(i)
            if (!stack.isEmpty) result[i + 1] = VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack)
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun getItemDetail(slot: Int): Map<String, *>? {
        assertBetween(slot, 1, container.containerSize, "slot")
        val stack = container.getItem(slot - 1)
        return if (stack.isEmpty) null else VanillaDetailRegistries.ITEM_STACK.getDetails(stack)
    }

    @LuaFunction(mainThread = true)
    fun getItemLimit(slot: Int): Int {
        assertBetween(slot, 1, container.containerSize, "slot")
        return container.getItem(slot - 1).maxStackSize
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pushItems(computer: IComputerAccess, toName: String, fromSlot: Int, limit: Optional<Int>, toSlot: Optional<Int>): Int {

        // Find location to transfer to
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = ExtractorProxy.extractCCItemStorage(level, location.target)
            ?: throw LuaException("Target '$toName' is not an inventory")

        // Validate slots

        // Validate slots
        val actualLimit: Int = limit.orElse(Int.MAX_VALUE)
        assertBetween(fromSlot, 1, container.containerSize, "fromtSlot")
        if (toSlot.isPresent)
            assertBetween(toSlot.get(), 1, toStorage.containerSize, "toSlot")

        return if (actualLimit <= 0) 0 else ContainerHelpers.moveBetweenInventories(
            container,
            fromSlot - 1,
            toStorage,
            toSlot.orElse(0) - 1,
            actualLimit
        )
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pullItems(computer: IComputerAccess, fromName: String, fromSlot: Int, limit: Optional<Int>, toSlot: Optional<Int>): Int {
        // Find location to transfer to
        val location =
            computer.getAvailablePeripheral(fromName) ?: throw LuaException("Source '$fromName' does not exist")
        val fromStorage = ExtractorProxy.extractCCItemStorage(level, location.target)
            ?: throw LuaException("Source '$fromName' is not an inventory")

        // Validate slots
        val actualLimit = limit.orElse(Int.MAX_VALUE)
        assertBetween(fromSlot, 1, fromStorage.containerSize, "fromSlot")
        if (toSlot.isPresent)
            assertBetween(toSlot.get(),1, container.containerSize, "toSlot")
        return if (actualLimit <= 0) 0 else ContainerHelpers.moveBetweenInventories(
            fromStorage,
            fromSlot - 1,
            container,
            toSlot.orElse(0) - 1,
            actualLimit
        )
    }
}