package site.siredvin.peripheralium.computercraft.turtle

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.TurtleIDBuildFunction
import site.siredvin.peripheralium.api.TurtlePeripheralBuildFunction
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.computercraft.pocket.StatefulPeripheralPocketUpgrade
import site.siredvin.peripheralium.util.turtleAdjective

abstract class StatefulPeripheralTurtleUpgrade<T : IOwnedPeripheral<*>> : BaseTurtleUpgrade<T> {
    companion object {
        fun <T : IOwnedPeripheral<*>> dynamic(item: Item, constructor: TurtlePeripheralBuildFunction<T>, idBuilder: TurtleIDBuildFunction): StatefulPeripheralTurtleUpgrade<T> {
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
        turtleAdjective(id),
        item,
    )

    private class Dynamic<T : IOwnedPeripheral<*>>(
        turtleID: ResourceLocation,
        stack: ItemStack,
        private val constructor: TurtlePeripheralBuildFunction<T>,
    ) : StatefulPeripheralTurtleUpgrade<T>(turtleID, stack) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T {
            return constructor.build(turtle, side)
        }
    }

    override fun getUpgradeData(stack: ItemStack): CompoundTag {
        return stack.getTagElement(StatefulPeripheralPocketUpgrade.STORED_DATA_TAG) ?: return CompoundTag()
    }

    override fun getUpgradeItem(upgradeData: CompoundTag): ItemStack {
        if (upgradeData.isEmpty) return craftingItem
        val base = craftingItem.copy()
        base.addTagElement(StatefulPeripheralPocketUpgrade.STORED_DATA_TAG, upgradeData)
        return base
    }

    override fun isItemSuitable(stack: ItemStack): Boolean {
        if (stack.getTagElement(StatefulPeripheralPocketUpgrade.STORED_DATA_TAG) == null) return super.isItemSuitable(stack)
        val tweakedStack = stack.copy()
        tweakedStack.orCreateTag.remove(StatefulPeripheralPocketUpgrade.STORED_DATA_TAG)
        return super.isItemSuitable(tweakedStack)
    }
}
