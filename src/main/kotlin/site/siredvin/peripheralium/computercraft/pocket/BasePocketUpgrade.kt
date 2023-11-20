package site.siredvin.peripheralium.computercraft.pocket

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.AbstractPocketUpgrade
import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.computercraft.peripheral.DisabledPeripheral
import site.siredvin.peripheralium.util.pocketAdjective
import java.util.function.Supplier

abstract class BasePocketUpgrade<T : IOwnedPeripheral<*>> : AbstractPocketUpgrade {
    protected var peripheral: T? = null

    protected constructor(id: ResourceLocation, adjective: String?, stack: Supplier<out ItemLike?>?) : super(
        id,
        adjective,
        stack,
    ) {
    }

    protected constructor(id: ResourceLocation, stack: Supplier<out ItemLike?>?) : super(
        id,
        pocketAdjective(id),
        stack,
    ) {
    }

    protected abstract fun getPeripheral(access: IPocketAccess?): T
    override fun createPeripheral(access: IPocketAccess): IPeripheral? {
        peripheral = getPeripheral(access)
        return if (!peripheral!!.isEnabled) DisabledPeripheral else peripheral
    }
}
