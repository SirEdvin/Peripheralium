package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.storages.item.AccessibleItemStorage
import site.siredvin.peripheralium.storages.item.ItemStorageUtils
import java.util.*
import java.util.function.Predicate
import kotlin.test.assertEquals

abstract class StorageTests {

    abstract fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage

    fun createStorage(sizes: List<Int>, stack: ItemStack, secondary: Boolean): AccessibleItemStorage {
        return createStorage(
            sizes.map {
                if (it == 0) {
                    ItemStack.EMPTY
                } else {
                    stack.copyWithCount(it)
                }
            },
            secondary,
        )
    }

    data class MoveArguments(
        val initialFrom: List<Int>,
        val initialTo: List<Int>,
        val moveLimit: Int,
        val expectedMoveAmount: Int,
        val expectedFrom: List<Int>,
        val expectedTo: List<Int>,
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
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64),
                        listOf(64, 64, 0),
                        64,
                        64,
                        listOf(64, 64),
                        listOf(64, 64, 64),
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64),
                        listOf(64, 64, 64),
                        128,
                        0,
                        listOf(64, 64, 64),
                        listOf(64, 64, 64),
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64),
                        listOf(64, 64, 32),
                        128,
                        32,
                        listOf(32, 64, 64),
                        listOf(64, 64, 64),
                    ),
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveTo(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createStorage(argument.initialFrom, grassBlock, secondary = false)
        val to = createStorage(argument.initialTo, grassBlock, secondary = true)
        val movedAmount = from.moveTo(to, argument.moveLimit, takePredicate = ItemStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveFrom(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createStorage(argument.initialFrom, grassBlock, secondary = false)
        val to = createStorage(argument.initialTo, grassBlock, secondary = true)
        val movedAmount = to.moveFrom(from, argument.moveLimit, takePredicate = ItemStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(listOf(ItemStack(Items.GRASS_BLOCK, 64), ItemStack(Items.WRITABLE_BOOK, 1), ItemStack(Items.GLASS_BOTTLE, 32)), true)
        val to = createStorage(listOf(64, 0, 0), ItemStack(Items.GRASS_BLOCK, 64), true)
        val predicate: Predicate<ItemStack> = Predicate {
            it.`is`(Items.WRITABLE_BOOK) || it.`is`(Items.WRITTEN_BOOK)
        }
        val movedAmount = from.moveTo(to, 1, takePredicate = predicate)
        assertEquals(1, movedAmount)
        StorageTestHelpers.assertStorage(from, listOf(64, 32), "from")
        StorageTestHelpers.assertStorage(to, listOf(64, 1), "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(listOf(ItemStack(Items.GRASS_BLOCK, 64), ItemStack(Items.WRITABLE_BOOK, 1), ItemStack(Items.GLASS_BOTTLE, 32)), true)
        val to = createStorage(listOf(64, 0, 0), ItemStack(Items.GRASS_BLOCK, 64), true)
        val predicate: Predicate<ItemStack> = Predicate {
            it.`is`(Items.REDSTONE)
        }
        val movedAmount = from.moveTo(to, 1, takePredicate = predicate)
        assertEquals(0, movedAmount)
        StorageTestHelpers.assertStorage(from, listOf(64, 1, 32), "from")
        StorageTestHelpers.assertStorage(to, listOf(64), "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }
}
