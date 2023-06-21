package site.siredvin.peripheralium.storages.fluid

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

data class FluidStack(val fluid: Fluid, var amount: Long, var tag: CompoundTag? = null) {
    companion object {
        val EMPTY = FluidStack(Fluids.EMPTY, 0)
        fun isSameFluid(first: FluidStack, second: FluidStack): Boolean {
            return first.fluid.isSame(second.fluid)
        }

        fun isSameFluidSameTags(first: FluidStack, second: FluidStack): Boolean {
            if (!isSameFluid(first, second)) {
                return false
            }
            return first.tag == second.tag
        }
    }
    val isEmpty: Boolean
        get() = fluid.isSame(Fluids.EMPTY)

    val platformAmount: Long
        get() = (this.amount * PeripheraliumPlatform.fluidCompactDivider).toLong()

    fun copy(): FluidStack {
        return FluidStack(fluid, amount, tag?.copy())
    }

    fun copyWithCount(count: Long): FluidStack {
        return FluidStack(fluid, count, tag?.copy())
    }

    fun grow(amount: Int) {
        this.amount += amount.toLong()
    }

    fun shrink(amount: Int) {
        this.amount -= amount.toLong()
    }

    fun grow(amount: Long) {
        this.amount += amount
    }

    fun shrink(amount: Long) {
        this.amount -= amount
    }
}
