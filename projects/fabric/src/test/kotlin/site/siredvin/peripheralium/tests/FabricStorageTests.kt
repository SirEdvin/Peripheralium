package site.siredvin.peripheralium.tests

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.storage.DummyStorage
import site.siredvin.peripheralium.storage.FabricSlottedStorageWrapper
import site.siredvin.peripheralium.storage.FabricStorageWrapper
import site.siredvin.peripheralium.storage.TestableStorage


internal class TweakedFabricStorageWrapper(private val inventoryStorage: InventoryStorage): FabricStorageWrapper(inventoryStorage), TestableStorage {
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

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): TestableStorage {
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

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): TestableStorage {
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

