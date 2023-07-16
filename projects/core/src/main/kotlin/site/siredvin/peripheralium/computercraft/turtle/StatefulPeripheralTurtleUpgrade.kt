package site.siredvin.peripheralium.computercraft.turtle

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.api.turtle.TurtleUpgradeIDSupplier
import site.siredvin.peripheralium.api.turtle.TurtleUpgradePeripheralBuilder

abstract class StatefulPeripheralTurtleUpgrade<T : IOwnedPeripheral<*>> : StatefulTurtleUpgrade<T> {
    companion object {
        fun <T : IOwnedPeripheral<*>> dynamic(item: Item, constructor: TurtleUpgradePeripheralBuilder<T>, idBuilder: TurtleUpgradeIDSupplier): StatefulPeripheralTurtleUpgrade<T> {
            return Dynamic(idBuilder.get(item), item.defaultInstance, constructor)
        }
    }
    constructor(id: ResourceLocation, adjective: String, item: ItemStack) : super(
        id,
        TurtleUpgradeType.PERIPHERAL,
        adjective,
        item,
    )

    constructor(id: ResourceLocation, item: ItemStack) : super(
        id,
        TurtleUpgradeType.PERIPHERAL,
        item,
    )

    private class Dynamic<T : IOwnedPeripheral<*>>(
        turtleID: ResourceLocation,
        stack: ItemStack,
        private val constructor: TurtleUpgradePeripheralBuilder<T>,
    ) : StatefulPeripheralTurtleUpgrade<T>(turtleID, stack) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T {
            return constructor.build(turtle, side)
        }
    }
}
