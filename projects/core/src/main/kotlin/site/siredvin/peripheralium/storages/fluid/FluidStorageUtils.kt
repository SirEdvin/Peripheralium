package site.siredvin.peripheralium.storages.fluid

import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object FluidStorageUtils {

    val ALWAYS: Predicate<FluidStack> = Predicate { true }

    fun naiveMove(from: FluidStorage, to: FluidSink, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        // Get stack to move
        val stack = from.takeFluid(takePredicate, limit)
        if (stack.isEmpty) {
            return 0
        }

        val stackCount = stack.amount

        // Move item to
        val remainder = to.storeFluid(stack)

        // Calculate items moved
        val count = stackCount - remainder.amount
        if (!remainder.isEmpty) {
            // Put reminder back
            from.storeFluid(remainder)
        }
        return count
    }

    fun canStack(first: FluidStack, second: FluidStack): Boolean {
        if (!FluidStack.isSameFluid(first, second)) {
            return false
        }
        return FluidStack.isSameFluidSameTags(first, second)
    }

    fun canMerge(first: FluidStack, second: FluidStack, stackLimit: Long = -1): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == -1L) Long.MAX_VALUE else minOf(stackLimit, Long.MAX_VALUE)
        return first.amount < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: FluidStack, second: FluidStack, mergeLimit: Long = Long.MAX_VALUE): FluidStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.amount, mergeLimit - first.amount)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty) {
            return FluidStack.EMPTY
        }
        return second
    }
}
