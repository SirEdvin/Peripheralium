package site.siredvin.peripheralium.tests

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.storage.DummyStorage

@WithMinecraft
internal class BaseStorageTests: StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): TestableStorage {
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class BaseSlottedStorageTests: SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return TargetableContainer(container)
    }
}