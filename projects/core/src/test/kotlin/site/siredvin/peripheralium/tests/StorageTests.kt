package site.siredvin.peripheralium.tests

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.storage.DummyStorage
import site.siredvin.peripheralium.storages.ContainerWrapper
import site.siredvin.peripheralium.storages.item.AccessibleItemStorage
import site.siredvin.peripheralium.storages.item.SlottedItemStorage

@WithMinecraft
internal class BaseStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class BaseSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedItemStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return ContainerWrapper(container)
    }
}

@WithMinecraft
internal class VerificationStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return ContainerWrapper(container)
        }
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class ReverseVerificationStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        if (!secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return ContainerWrapper(container)
        }
        return DummyStorage(items.size, items)
    }
}
