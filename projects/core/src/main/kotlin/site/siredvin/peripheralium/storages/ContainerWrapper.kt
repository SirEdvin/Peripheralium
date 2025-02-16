package site.siredvin.peripheralium.storages

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import java.util.function.Predicate

class ContainerWrapper(private val container: Container) : SlottedItemStorage {
    override fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack = ContainerUtils.takeItems(container, limit, startSlot, endSlot, predicate)

    override fun getItem(slot: Int): ItemStack = container.getItem(slot)

    override fun canPlaceItem(slot: Int, item: ItemStack): Boolean = container.canPlaceItem(slot, item)

    override fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack = ContainerUtils.storeItem(container, stack, startSlot, endSlot)

    override fun setChanged() {
        container.setChanged()
    }

    override val size: Int
        get() = container.containerSize
}
