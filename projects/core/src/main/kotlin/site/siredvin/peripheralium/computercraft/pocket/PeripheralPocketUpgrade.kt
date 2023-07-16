package site.siredvin.peripheralium.computercraft.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.api.pocket.PockerUpgradePeripheralBuilder
import site.siredvin.peripheralium.util.pocketAdjective

class PeripheralPocketUpgrade<T : IOwnedPeripheral<*>> : BasePocketUpgrade<T> {

    private val constructor: PockerUpgradePeripheralBuilder<T>

    constructor(id: ResourceLocation, adjective: String, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>) : super(
        id,
        adjective,
        stack,
    ) {
        this.constructor = constructor
    }

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>) : super(
        id,
        pocketAdjective(id),
        stack,
    ) {
        this.constructor = constructor
    }

    override fun getPeripheral(access: IPocketAccess): T {
        return constructor.build(access)
    }
}
