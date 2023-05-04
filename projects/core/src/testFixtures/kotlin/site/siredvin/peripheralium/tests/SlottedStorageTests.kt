package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.storage.TestableStorage
import java.util.function.Predicate
import kotlin.test.assertEquals

abstract class SlottedStorageTests: StorageTests() {

    internal class TestableWrap(private val storage: SlottedStorage): TestableStorage {
        override fun getItem(slot: Int): ItemStack {
            return storage.getItem(slot)
        }

        override fun getItems(): Iterator<ItemStack> {
            return storage.getItems()
        }

        override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack {
            return storage.takeItems(predicate, limit)
        }

        override fun storeItem(stack: ItemStack): ItemStack {
            return storage.storeItem(stack)
        }

        override fun setChanged() {
            storage.setChanged()
        }

    }

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): TestableStorage {
        return TestableWrap(createSlottedStorage(items, secondary))
    }
    abstract fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage

    fun createSlottedStorage(sizes: List<Int>, stack: ItemStack, secondary: Boolean): SlottedStorage {
        return createSlottedStorage(sizes.map {
            if (it == 0)
                ItemStack.EMPTY
            else
                stack.copyWithCount(it)
        }, secondary)
    }

    data class MoveArguments(
        val initialFrom: List<Int>, val initialTo: List<Int>,
        val moveLimit: Int, val expectedMoveAmount: Int,
        val expectedFrom: List<Int>, val expectedTo: List<Int>,
        val fromSlot: Int = -1, val toSlot: Int = -1,
    )

    companion object {
        @JvmStatic
        fun generateMoveSlottedParameters(): List<Arguments> {
            /**
             * Test cases:
             *  - Partial limit move (one stack instead of two)
             *  - Partial limit move (zero stack instead of one)
             *  - Partial limit move (two stacks with two stacks)
             */
            return listOf(
                Arguments.of(
                    MoveArguments(
                        listOf(64, 0, 64), listOf(64, 64, 0),
                        128, 64,
                        listOf(0, 0, 64), listOf(64, 64, 64),
                        0, 2
                    )
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64), listOf(64, 64, 64),
                        128, 0,
                        listOf(64, 64, 64), listOf(64, 64, 64),
                        1, 2
                    )
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64), listOf(64, 64, 32),
                        128, 32,
                        listOf(32, 64, 64), listOf(64, 64, 64),
                        0, 2
                    )
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveSlottedParameters")
    fun testMoveToSlotted(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createSlottedStorage(argument.initialFrom, grassBlock, false)
        val to = createSlottedStorage(argument.initialTo, grassBlock, true)
        val movedAmount = from.moveTo(to, argument.moveLimit, argument.fromSlot, argument.toSlot, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertSlottedStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertSlottedStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @ParameterizedTest
    @MethodSource("generateMoveSlottedParameters")
    fun testMoveFromSlotted(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createSlottedStorage(argument.initialFrom, grassBlock, false)
        val to = createSlottedStorage(argument.initialTo, grassBlock, true)
        val movedAmount = to.moveFrom(from, argument.moveLimit, argument.toSlot, argument.fromSlot, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertSlottedStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertSlottedStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }
}