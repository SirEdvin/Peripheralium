package site.siredvin.peripheralium.tests

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.wrapper.InvWrapper
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.storage.ItemHandlerWrapper

@WithMinecraft
class ForgeSlottedStorageTests: SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        val baseContainer = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            baseContainer.setItem(index, itemStack)
        }
        return ItemHandlerWrapper(InvWrapper(baseContainer))
    }
}


@WithMinecraft
class CompactForgeSlottedStorageTests: SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty)
                    container.setItem(index, itemStack)
            }
            return TargetableContainer(container)
        }
        val baseContainer = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            baseContainer.setItem(index, itemStack)
        }
        return ItemHandlerWrapper(InvWrapper(baseContainer))
    }
}
