package site.siredvin.peripheralium.computercraft.turtle

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.computercraft.peripheral.DisabledPeripheral

abstract class BaseTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: String,
    stack: ItemStack,
) : AbstractTurtleUpgrade(id, type, adjective, stack) {

    protected abstract fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T

    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral? {
        val peripheral = buildPeripheral(turtle, side)
        return if (!peripheral.isEnabled) { DisabledPeripheral } else peripheral
    }
}
