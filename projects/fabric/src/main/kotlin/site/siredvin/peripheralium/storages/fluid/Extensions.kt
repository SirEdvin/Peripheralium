package site.siredvin.peripheralium.storages.fluid

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import kotlin.math.roundToLong

fun StorageView<FluidVariant>.toVanilla(): FluidStack {
    return this.resource.toVanilla(this.amount)
}

fun FluidVariant.toVanilla(count: Long = 1L): FluidStack {
    return FluidStack(this.fluid, (count * PeripheraliumPlatform.fluidCompactDivider).roundToLong(), this.nbt)
}

fun FluidStack.toVariant(): FluidVariant {
    return FluidVariant.of(this.fluid, this.tag)
}
