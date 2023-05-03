package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.tests.storage.DummySlottedStorage
import site.siredvin.peripheralium.tests.storage.DummyStorage
import kotlin.test.assertEquals

@ExtendWith(MinecraftBootstrap::class)
internal class StorageTests {

    data class MoveToArguments(
        val initialFrom: List<Int>, val initialTo: List<Int>,
        val moveLimit: Int, val expectedMoveAmount: Int,
        val expectedFrom: List<Int>, val expectedTo: List<Int>,
        val fromSlot: Int = -1, val toSlot: Int = -1,
    )

    companion object {
        @JvmStatic
        fun generateMoveToParameters(): List<Arguments> {
            /**
             * Test cases:
             *  - Partial limit move (one stack instead of two)
             *  - Partial limit move (zero stack instead of one)
             *  - Partial limit move (two stacks with two stacks)
             *  - Partial limit move (half stack instead of two stacks)
             *  - Partial limit move (1.5 stack instead of two stacks)
             */
            return listOf(
                Arguments.of(MoveToArguments(
                    listOf(64, 64, 64), listOf(64, 64, 0),
                    128, 64,
                    listOf(64, 64), listOf(64, 64, 64)
                )),
                Arguments.of(MoveToArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 64),
                    128, 0,
                    listOf(64, 64, 64), listOf(64, 64, 64)
                )),
                // TODO: This is not critical, but ideally this function should be able to move limit above (!) maxStackSize
//                Arguments.of(MoveToArguments(
//                    listOf(64, 64, 64), listOf(0, 0, 0),
//                    128, 128,
//                    listOf(64), listOf(64, 64)
//                )),
                Arguments.of(MoveToArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 32),
                    128, 32,
                    listOf(64, 64, 32), listOf(64, 64, 64)
                )),
            )
        }

        @JvmStatic
        fun generateMoveToSlottedParameters(): List<Arguments> {
            /**
             * Test cases:
             *  - Partial limit move (one stack instead of two)
             *  - Partial limit move (zero stack instead of one)
             *  - Partial limit move (two stacks with two stacks)
             *  - Partial limit move (half stack instead of two stacks)
             *  - Partial limit move (1.5 stack instead of two stacks)
             */
            return listOf(
                Arguments.of(MoveToArguments(
                    listOf(64, 0, 64), listOf(64, 64, 0),
                    128, 64,
                    listOf(0, 0, 64), listOf(64, 64, 64),
                    0, 2
                )),
                Arguments.of(MoveToArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 64),
                    128, 0,
                    listOf(64, 64, 64), listOf(64, 64, 64),
                    1, 2
                )),
                // TODO: This is not critical, but ideally this function should be able to move limit above (!) maxStackSize
//                Arguments.of(MoveToArguments(
//                    listOf(64, 64, 64), listOf(0, 0, 0),
//                    128, 128,
//                    listOf(64), listOf(64, 64)
//                )),
                Arguments.of(MoveToArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 32),
                    128, 32,
                    listOf(32, 64, 64), listOf(64, 64, 64),
                    0, 2
                )),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveTo(argument: MoveToArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = DummyStorage(argument.initialFrom, grassBlock)
        val to = DummyStorage(argument.initialTo, grassBlock)
        val movedAmount = from.moveTo(to, argument.moveLimit, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        from.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedFrom[index], stack.count)
        }
        to.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedTo[index], stack.count)
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveFrom(argument: MoveToArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = DummyStorage(argument.initialFrom, grassBlock)
        val to = DummyStorage(argument.initialTo, grassBlock)
        val movedAmount = to.moveFrom(from, argument.moveLimit, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        from.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedFrom[index], stack.count)
        }
        to.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedTo[index], stack.count)
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToSlottedParameters")
    fun testMoveToSlotted(argument: MoveToArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = DummySlottedStorage(argument.initialFrom, grassBlock)
        val to = DummySlottedStorage(argument.initialTo, grassBlock)
        val movedAmount = from.moveTo(to, argument.moveLimit, argument.fromSlot, argument.toSlot, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        from.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedFrom[index], stack.count)
        }
        to.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedTo[index], stack.count)
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToSlottedParameters")
    fun testMoveFromSlotted(argument: MoveToArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = DummySlottedStorage(argument.initialFrom, grassBlock)
        val to = DummySlottedStorage(argument.initialTo, grassBlock)
        val movedAmount = to.moveFrom(from, argument.moveLimit, argument.toSlot, argument.fromSlot, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        from.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedFrom[index], stack.count)
        }
        to.items.forEachIndexed { index, stack ->
            assertEquals(argument.expectedTo[index], stack.count)
        }
    }
}