package site.siredvin.peripheralium.computercraft.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.PocketPeripheralBuildFunction
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.util.pocketAdjective

class StatefulPeripheralPocketUpgrade<T : IOwnedPeripheral<*>> : BasePocketUpgrade<T> {

    companion object {
        val STORED_DATA_TAG = "storedData"
    }

    private val constructor: PocketPeripheralBuildFunction<T>

    constructor(id: ResourceLocation, adjective: String, stack: ItemStack, constructor: PocketPeripheralBuildFunction<T>) : super(
        id,
        adjective,
        stack,
    ) {
        this.constructor = constructor
    }

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PocketPeripheralBuildFunction<T>) : super(
        id,
        pocketAdjective(id),
        stack,
    ) {
        this.constructor = constructor
    }

    override fun getPeripheral(access: IPocketAccess): T {
        return constructor.build(access)
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
