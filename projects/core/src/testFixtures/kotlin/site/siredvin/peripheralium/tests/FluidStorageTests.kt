package site.siredvin.peripheralium.tests

import net.minecraft.world.level.material.Fluids
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.storages.fluid.FluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStorageUtils
import java.util.function.Predicate
import kotlin.test.assertEquals

abstract class FluidStorageTests {
    abstract fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage

    fun createStorage(sizes: List<Long>, stack: FluidStack, secondary: Boolean): FluidStorage {
        return createStorage(
            sizes.map {
                if (it == 0L) {
                    FluidStack.EMPTY
                } else {
                    stack.copyWithCount(it)
                }
            },
            secondary,
        )
    }

    data class MoveArguments(
        val initialFrom: List<Long>,
        val initialTo: List<Long>,
        val moveLimit: Long,
        val expectedMoveAmount: Long,
        val expectedFrom: List<Long>,
        val expectedTo: List<Long>,
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
                        listOf(1000, 1000, 1000),
                        listOf(1000, 1000, 0),
                        1000,
                        1000,
                        listOf(1000, 1000),
                        listOf(1000, 1000, 1000),
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(1000, 1000, 1000),
                        listOf(1000, 1000, 1000),
                        2000,
                        0,
                        listOf(1000, 1000, 1000),
                        listOf(1000, 1000, 1000),
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(1000, 1000, 1000),
                        listOf(1000, 1000, 500),
                        2000,
                        500,
                        listOf(500, 1000, 1000),
                        listOf(1000, 1000, 1000),
                    ),
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveTo(argument: MoveArguments) {
        val water = FluidStack(Fluids.WATER, 1000)
        val from = createStorage(argument.initialFrom, water, secondary = false)
        val to = createStorage(argument.initialTo, water, secondary = true)
        val movedAmount = from.moveTo(to, argument.moveLimit, takePredicate = FluidStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertFluidStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertFluidStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveFrom(argument: MoveArguments) {
        val water = FluidStack(Fluids.WATER, 1000)
        val from = createStorage(argument.initialFrom, water, secondary = false)
        val to = createStorage(argument.initialTo, water, secondary = true)
        val movedAmount = to.moveFrom(from, argument.moveLimit, takePredicate = FluidStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertFluidStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertFluidStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(
            listOf(
                FluidStack(Fluids.WATER, 1000),
                FluidStack(Fluids.LAVA, 1),
                FluidStack(Fluids.WATER, 500),
            ),
            true,
        )
        val to = createStorage(listOf(1000, 0, 0), FluidStack(Fluids.WATER, 1000), true)
        val predicate: Predicate<FluidStack> = Predicate {
            it.fluid.isSame(Fluids.LAVA)
        }
        val movedAmount = from.moveTo(to, 1, takePredicate = predicate)
        assertEquals(1, movedAmount)
        StorageTestHelpers.assertFluidStorage(from, listOf(1000, 500), "from")
        StorageTestHelpers.assertFluidStorage(to, listOf(1000, 1), "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(
            listOf(
                FluidStack(Fluids.WATER, 500),
                FluidStack(Fluids.WATER, 500),
                FluidStack(Fluids.WATER, 500),
            ),
            true,
        )
        val to = createStorage(listOf(1000, 0, 0), FluidStack(Fluids.WATER, 1000), true)
        val predicate: Predicate<FluidStack> = Predicate {
            it.fluid.isSame(Fluids.LAVA)
        }
        val movedAmount = from.moveTo(to, 1, takePredicate = predicate)
        assertEquals(0, movedAmount)
        StorageTestHelpers.assertFluidStorage(from, listOf(500, 500, 500), "from")
        StorageTestHelpers.assertFluidStorage(to, listOf(1000), "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }
}
