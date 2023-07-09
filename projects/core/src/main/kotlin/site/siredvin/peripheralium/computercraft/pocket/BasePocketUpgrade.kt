package site.siredvin.peripheralium.computercraft.pocket

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.AbstractPocketUpgrade
import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.computercraft.peripheral.DisabledPeripheral
import site.siredvin.peripheralium.util.pocketAdjective

abstract class BasePocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: String = pocketAdjective(id),
    stack: ItemStack,
) : AbstractPocketUpgrade(id, adjective, stack) {
    protected var peripheral: T? = null

    protected abstract fun getPeripheral(access: IPocketAccess): T
    override fun createPeripheral(access: IPocketAccess): IPeripheral? {
        peripheral = getPeripheral(access)
        return if (!peripheral!!.isEnabled) DisabledPeripheral else peripheral
    }
}
