package site.siredvin.peripheralium.tests

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.wrapper.InvWrapper
import site.siredvin.peripheralium.storages.ContainerWrapper
import site.siredvin.peripheralium.storages.item.ItemHandlerWrapper
import site.siredvin.peripheralium.storages.item.SlottedItemStorage

@WithMinecraft
class ForgeSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedItemStorage {
        val baseContainer = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            baseContainer.setItem(index, itemStack)
        }
        return ItemHandlerWrapper(InvWrapper(baseContainer))
    }
}

@WithMinecraft
class CompactForgeSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedItemStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return ContainerWrapper(container)
        }
        val baseContainer = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            baseContainer.setItem(index, itemStack)
        }
        return ItemHandlerWrapper(InvWrapper(baseContainer))
    }
}
