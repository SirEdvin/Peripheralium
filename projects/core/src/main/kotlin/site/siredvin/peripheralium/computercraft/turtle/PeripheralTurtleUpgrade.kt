package site.siredvin.peripheralium.computercraft.turtle

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.TurtleIDBuildFunction
import site.siredvin.peripheralium.api.TurtlePeripheralBuildFunction
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.util.turtleAdjective

abstract class PeripheralTurtleUpgrade<T : IOwnedPeripheral<*>> : BaseTurtleUpgrade<T> {
    constructor(id: ResourceLocation, adjective: String, item: ItemStack) : super(
        id,
        TurtleUpgradeType.PERIPHERAL,
        adjective,
        item
    )

    constructor(id: ResourceLocation, item: ItemStack) : super(
        id,
        TurtleUpgradeType.PERIPHERAL,
        turtleAdjective(id),
        item
    )

    companion object {
        fun <T : IOwnedPeripheral<*>> dynamic(item: Item, constructor: TurtlePeripheralBuildFunction<T>, idBuilder: TurtleIDBuildFunction): PeripheralTurtleUpgrade<T> {
            return Dynamic(idBuilder.get(item), item.defaultInstance, constructor)
        }
    }

    private class Dynamic<T : IOwnedPeripheral<*>>(
        turtleID: ResourceLocation, stack: ItemStack, private val constructor: TurtlePeripheralBuildFunction<T>
        ): PeripheralTurtleUpgrade<T>(turtleID, stack) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T {
            return constructor.build(turtle, side)
        }

    }
}