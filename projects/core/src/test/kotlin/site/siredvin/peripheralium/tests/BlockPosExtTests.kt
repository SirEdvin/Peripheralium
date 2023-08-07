package site.siredvin.peripheralium.tests

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.peripheralium.ext.fromRelative
import site.siredvin.peripheralium.ext.toRelative
import kotlin.test.assertEquals

@WithMinecraft
class BlockPosExtTests {

    companion object {
        @JvmStatic
        fun generateDirections(): List<Direction> {
            return listOf(
                Direction.EAST,
                Direction.WEST,
                Direction.NORTH,
                Direction.SOUTH,
            )
        }
    }

    @ParameterizedTest
    @MethodSource("generateDirections")
    fun relativeBlockTesting(direction: Direction) {
        val initialBlockPoses = mutableListOf(BlockPos(0, 0, 0))
        for (i in 0..3) {
            initialBlockPoses.add(BlockPos(i, 0, 0))
            initialBlockPoses.add(BlockPos(-i, 1, 0))
            initialBlockPoses.add(BlockPos(0, 2, i))
            initialBlockPoses.add(BlockPos(0, 3, -i))
            initialBlockPoses.add(BlockPos(i, 4, i))
            initialBlockPoses.add(BlockPos(i, 5, -i))
            initialBlockPoses.add(BlockPos(-i, 6, i))
            initialBlockPoses.add(BlockPos(-i, 7, -i))
        }
        val transformedPoses = initialBlockPoses.map { it.toRelative(direction).fromRelative(direction) }
        assertEquals(initialBlockPoses, transformedPoses)
    }
}
