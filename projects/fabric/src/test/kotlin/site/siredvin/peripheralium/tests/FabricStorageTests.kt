package site.siredvin.peripheralium.tests

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.storage.FabricSlottedStorageWrapper
import kotlin.test.assertEquals

@ExtendWith(FabricBootstrap::class)
@ExtendWith(FabricMinecraftBootstrap::class)
internal class FabricStorageTests {

    data class MoveToArguments(
        val initialFrom: List<Int>, val initialTo: List<Int>,
        val moveLimit: Int, val expectedMoveAmount: Int,
        val expectedFrom: List<Int>, val expectedTo: List<Int>,
        val fromSlot: Int = -1, val toSlot: Int = -1,
    )

    companion object {
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
                Arguments.of(
                    MoveToArguments(
                    listOf(64, 0, 64), listOf(64, 64, 0),
                    128, 64,
                    listOf(0, 0, 64), listOf(64, 64, 64),
                    0, 2
                )
                ),
                Arguments.of(
                    MoveToArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 64),
                    128, 0,
                    listOf(64, 64, 64), listOf(64, 64, 64),
                    1, 2
                )
                ),
                Arguments.of(
                    MoveToArguments(
                    listOf(64, 64, 64),  listOf(64, 64, 32),
                    128, 32,
                    listOf(32, 64, 64), listOf(64, 64, 64),
                    0, 2
                )
                ),
            )
        }

        fun buildStorage(sizes: List<Int>, stack: ItemStack): FabricSlottedStorageWrapper {
            val container = SimpleContainer(3)
            sizes.forEachIndexed { index, i ->
                if (i > 0)
                    container.setItem(index, stack.copyWithCount(i))
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
    }


    @ParameterizedTest
    @MethodSource("generateMoveToSlottedParameters")
    fun testMoveToSlotted(argument: MoveToArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = buildStorage(argument.initialFrom, grassBlock)
        val to = buildStorage(argument.initialTo, grassBlock)
        val movedAmount = from.moveTo(to, argument.moveLimit, argument.fromSlot, argument.toSlot, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        argument.expectedFrom.forEachIndexed { index, amount ->
            assertEquals(amount, from.getItem(index).count)
        }
        argument.expectedTo.forEachIndexed { index, amount ->
            assertEquals(amount, to.getItem(index).count)
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToSlottedParameters")
    fun testMoveFromSlotted(argument: MoveToArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = buildStorage(argument.initialFrom, grassBlock)
        val to = buildStorage(argument.initialTo, grassBlock)
        val movedAmount = to.moveFrom(from, argument.moveLimit, argument.toSlot, argument.fromSlot, takePredicate = StorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        argument.expectedFrom.forEachIndexed { index, amount ->
            assertEquals(amount, from.getItem(index).count)
        }
        argument.expectedTo.forEachIndexed { index, amount ->
            assertEquals(amount, to.getItem(index).count)
        }
    }
}