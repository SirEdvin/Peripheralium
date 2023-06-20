package site.siredvin.peripheralium.tests

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.storage.DummyStorage
import site.siredvin.peripheralium.storages.ContainerWrapper
import site.siredvin.peripheralium.storages.item.AccessibleItemStorage
import site.siredvin.peripheralium.storages.item.FabricSlottedStorageWrapper
import site.siredvin.peripheralium.storages.item.FabricStorageWrapper
import site.siredvin.peripheralium.storages.item.SlottedItemStorage

internal class TweakedFabricStorageWrapper(private val inventoryStorage: InventoryStorage) :
    FabricStorageWrapper(inventoryStorage),
    AccessibleItemStorage {
    override fun getItem(slot: Int): ItemStack {
        val variantInSlot = inventoryStorage.getSlot(slot)
        return variantInSlot.resource.toStack(variantInSlot.amount.toInt())
    }
}

@WithMinecraft
internal class FabricSlottedStorageTests : SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedItemStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class FabricStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class CompactFabricSlottedStorageTests : SlottedStorageTests() {

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
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class CompactFabricStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        if (secondary) {
            return DummyStorage(items.size, items)
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class VerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class ReverseVerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleItemStorage {
        if (!secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}
