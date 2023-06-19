package site.siredvin.peripheralium.storages.fluid

import net.minecraftforge.fluids.FluidStack as ForgeFluidStack

fun ForgeFluidStack.toVanilla(): FluidStack {
    if (this.isEmpty) return FluidStack.EMPTY
    return FluidStack(this.fluid, this.amount.toLong(), this.tag)
}

fun ForgeFluidStack.copyWithCount(count: Int): ForgeFluidStack {
    val copy = this.copy()
    copy.amount = count
    return copy
}

fun FluidStack.toForge(): ForgeFluidStack {
    if (this.isEmpty) return ForgeFluidStack.EMPTY
    return ForgeFluidStack(this.fluid, this.amount.toInt(), this.tag)
}
