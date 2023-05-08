package site.siredvin.peripheralium.tests

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.AccessibleStorage
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.storage.DummyStorage
import site.siredvin.peripheralium.storage.FabricSlottedStorageWrapper
import site.siredvin.peripheralium.storage.FabricStorageWrapper


internal class TweakedFabricStorageWrapper(private val inventoryStorage: InventoryStorage): FabricStorageWrapper(inventoryStorage),
    AccessibleStorage {
    override fun getItem(slot: Int): ItemStack {
        val variantInSlot = inventoryStorage.getSlot(slot)
        return variantInSlot.resource.toStack(variantInSlot.amount.toInt())
    }

}

@WithMinecraft
internal class FabricSlottedStorageTests: SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class FabricStorageTests: StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class CompactFabricSlottedStorageTests: SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty)
                    container.setItem(index, itemStack)
            }
            return TargetableContainer(container)
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class CompactFabricStorageTests: StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        if (secondary)
            return DummyStorage(items.size, items)
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class VerificationFabricStorageTests: StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty)
                    container.setItem(index, itemStack)
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
internal class ReverseVerificationFabricStorageTests: StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleStorage {
        if (!secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty)
                    container.setItem(index, itemStack)
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty)
                container.setItem(index, itemStack)
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

