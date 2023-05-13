package site.siredvin.peripheralium.computercraft.pocket

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.AbstractPocketUpgrade
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.TurtleIDBuildFunction
import site.siredvin.peripheralium.api.TurtlePeripheralBuildFunction
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.common.items.TurtleItem
import site.siredvin.peripheralium.computercraft.peripheral.DisabledPeripheral
import site.siredvin.peripheralium.computercraft.turtle.PeripheralTurtleUpgrade
import site.siredvin.peripheralium.util.pocketAdjective

abstract class BasePocketUpgrade<T : IOwnedPeripheral<*>> : AbstractPocketUpgrade {
    protected var peripheral: T? = null

    constructor(id: ResourceLocation, adjective: String, stack: ItemStack) : super(
        id,
        adjective,
        stack
    ) {
    }

    constructor(id: ResourceLocation, stack: ItemStack) : super(
        id,
        pocketAdjective(id),
        stack
    ) {
    }

    protected abstract fun getPeripheral(access: IPocketAccess): T
    override fun createPeripheral(access: IPocketAccess): IPeripheral? {
        peripheral = getPeripheral(access)
        return if (!peripheral!!.isEnabled) DisabledPeripheral else peripheral
    }
}