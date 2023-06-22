package site.siredvin.peripheralium.storages.energy

import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object EnergyStorageUtils {

    val ALWAYS: Predicate<EnergyStack> = Predicate { true }

    fun naiveMove(from: EnergyStorage, to: EnergySink, limit: Long, takePredicate: Predicate<EnergyStack>): Long {
        // Get stack to move
        val stack = from.takeEnergy(takePredicate, limit)
        if (stack.isEmpty) {
            return 0
        }

        val stackCount = stack.amount

        // Move item to
        val remainder = to.storeEnergy(stack)

        // Calculate items moved
        val count = stackCount - remainder.amount
        if (!remainder.isEmpty) {
            // Put reminder back
            from.storeEnergy(remainder)
        }
        return count
    }

    fun canStack(first: EnergyStack, second: EnergyStack): Boolean {
        return EnergyStack.isSameEnergy(first, second)
    }

    fun canMerge(first: EnergyStack, second: EnergyStack, stackLimit: Long = -1): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == -1L) Long.MAX_VALUE else minOf(stackLimit, Long.MAX_VALUE)
        return first.amount < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: EnergyStack, second: EnergyStack, mergeLimit: Long = Long.MAX_VALUE): EnergyStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.amount, mergeLimit - first.amount)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty) {
            return EnergyStack.EMPTY
        }
        return second
    }
}
