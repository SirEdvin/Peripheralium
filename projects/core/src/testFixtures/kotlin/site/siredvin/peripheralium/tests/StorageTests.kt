package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.storage.TestableStorage
import kotlin.test.assertEquals

abstract class StorageTests {

    abstract fun createStorage(items: List<ItemStack>, secondary: Boolean): TestableStorage

    fun createStorage(sizes: List<Int>, stack: ItemStack, secondary: Boolean): TestableStorage {
        return createStorage(sizes.map {
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
    )

    companion object {
        @JvmStatic
        fun generateMoveToParameters(): List<Arguments> {
            /**
             * Test cases:
             *  - Partial limit move (one stack instead of two)
             *  - Partial limit move (zero stack instead of one)
             *  - Partial limit move (two stacks with two stacks)
             */
            return listOf(
                Arguments.of(MoveArguments(
                    listOf(64, 64, 64), listOf(64, 64, 0),
                    128, 64,
                    listOf(64, 64), listOf(64, 64, 64)
                )),
                Arguments.of(MoveArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 64),
                    128, 0,
                    listOf(64, 64, 64), listOf(64, 64, 64)
                )),
                Arguments.of(MoveArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 32),
                    128, 32,
                    listOf(64, 64, 32), listOf(64, 64, 64)
                )),
            )
        }


    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveTo(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createStorage(argument.initialFrom, grassBlock, secondary = false)
        val to = createStorage(argument.initialTo, grassBlock, secondary = true)
        val movedAmount = from.moveTo(to, argument.moveLimit, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        argument.expectedFrom.forEachIndexed { index, amount ->
            assertEquals(amount, from.getItem(index).count)
        }
        argument.expectedTo.forEachIndexed { index, amount ->
            assertEquals(amount, to.getItem(index).count)
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveFrom(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createStorage(argument.initialFrom, grassBlock, secondary = false)
        val to = createStorage(argument.initialTo, grassBlock, secondary = true)
        val movedAmount = to.moveFrom(from, argument.moveLimit, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        argument.expectedFrom.forEachIndexed { index, amount ->
            assertEquals(amount, from.getItem(index).count)
        }
        argument.expectedTo.forEachIndexed { index, amount ->
            assertEquals(amount, to.getItem(index).count)
        }
    }
}