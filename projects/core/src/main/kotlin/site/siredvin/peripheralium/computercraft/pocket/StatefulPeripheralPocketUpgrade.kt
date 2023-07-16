package site.siredvin.peripheralium.computercraft.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.api.pocket.PockerUpgradePeripheralBuilder
import site.siredvin.peripheralium.util.pocketAdjective

class StatefulPeripheralPocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: String,
    stack: ItemStack,
    private val constructor: PockerUpgradePeripheralBuilder<T>,
) : StatefulPocketUpgrade<T>(id, adjective, stack) {

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>) : this(
        id,
        pocketAdjective(id),
        stack,
        constructor,
    )

    override fun getPeripheral(access: IPocketAccess): T {
        return constructor.build(access)
    }
}
