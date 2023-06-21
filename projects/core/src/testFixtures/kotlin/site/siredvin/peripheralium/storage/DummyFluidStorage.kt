package site.siredvin.peripheralium.storage

import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.storages.fluid.FluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStorageUtils
import java.util.function.Predicate

class DummyFluidStorage(private val maxSlots: Int, initialItems: List<FluidStack>) : FluidStorage {

    companion object {
        const val STACK_LIMIT = 1000L
    }

    val fluids: MutableList<FluidStack> = mutableListOf()

    init {
        if (initialItems.size > maxSlots) {
            throw IllegalArgumentException("Max slots is too low for you?")
        }
        initialItems.forEach {
            fluids.add(it)
        }
        clean()
    }

    fun clean() {
        fluids.removeIf { it.isEmpty }
    }

    override fun getFluids(): Iterator<FluidStack> {
        return fluids.iterator()
    }

    override fun takeFluid(predicate: Predicate<FluidStack>, limit: Long): FluidStack {
        var slidingStack = FluidStack.EMPTY
        var slidingLimit = limit
        val toRemove = mutableListOf<Int>()
        fluids.forEachIndexed { index, stack ->
            if (slidingLimit > 0) {
                if (!stack.isEmpty && predicate.test(stack)) {
                    if (slidingStack.isEmpty) {
                        slidingStack = stack
                        slidingLimit = limit - stack.amount
                        toRemove.add(index)
                    } else if (FluidStorageUtils.canMerge(slidingStack, stack, STACK_LIMIT)) {
                        val originalCount = stack.amount
                        val remainder = FluidStorageUtils.inplaceMerge(slidingStack, stack, STACK_LIMIT)
                        slidingLimit -= originalCount - remainder.amount
                        if (remainder.isEmpty) {
                            toRemove.add(index)
                        }
                    }
                }
            }
        }
        toRemove.asReversed().forEach {
            fluids.removeAt(it)
        }
        clean()
        return slidingStack
    }

    override fun storeFluid(stack: FluidStack): FluidStack {
        fluids.forEach {
            if (FluidStorageUtils.canMerge(it, stack, STACK_LIMIT)) {
                FluidStorageUtils.inplaceMerge(it, stack, STACK_LIMIT)
            }
        }
        if (stack.isEmpty) {
            return FluidStack.EMPTY
        }
        if (fluids.size < maxSlots) {
            fluids.add(stack)
            return FluidStack.EMPTY
        }
        return stack
    }

    override fun setChanged() {
    }
}
