package site.siredvin.peripheralium.computercraft.turtle

import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.computercraft.pocket.StatefulPocketUpgrade

abstract class StatefulTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: String,
    stack: ItemStack,
) : BaseTurtleUpgrade<T>(id, type, adjective, stack) {
    companion object {
        const val STORED_DATA_TAG = StatefulPocketUpgrade.STORED_DATA_TAG
    }
    override fun getUpgradeData(stack: ItemStack): CompoundTag {
        return stack.getTagElement(STORED_DATA_TAG) ?: return CompoundTag()
    }

    override fun getUpgradeItem(upgradeData: CompoundTag): ItemStack {
        if (upgradeData.isEmpty) return craftingItem
        val base = craftingItem.copy()
        base.addTagElement(STORED_DATA_TAG, upgradeData)
        return base
    }

    override fun isItemSuitable(stack: ItemStack): Boolean {
        if (stack.getTagElement(STORED_DATA_TAG) == null) return super.isItemSuitable(stack)
        val tweakedStack = stack.copy()
        tweakedStack.orCreateTag.remove(STORED_DATA_TAG)
        return super.isItemSuitable(tweakedStack)
    }
}
