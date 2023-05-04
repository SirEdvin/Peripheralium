package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.storage.DummySlottedStorage
import site.siredvin.peripheralium.storage.DummyStorage
import site.siredvin.peripheralium.storage.TestableStorage

@WithMinecraft
internal class BaseStorageTests: StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): TestableStorage {
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class BaseSlottedStorageTests: SlottedStorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        return DummySlottedStorage(items.size, items)
    }
}