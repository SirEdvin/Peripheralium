package site.siredvin.peripheralium.tests

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.AccessibleStorage
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.storage.DummyStorage

@WithMinecraft
internal class BaseStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class BaseSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TargetableContainer(container)
    }
}

@WithMinecraft
internal class VerificationStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return TargetableContainer(container)
        }
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class ReverseVerificationStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        if (!secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return TargetableContainer(container)
        }
        return DummyStorage(items.size, items)
    }
}
