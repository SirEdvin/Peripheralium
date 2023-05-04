package site.siredvin.peripheralium.tests

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.extension.ExtendWith
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.storage.*


internal class TweakedFabricStorageWrapper(private val inventoryStorage: InventoryStorage): FabricStorageWrapper(inventoryStorage), TestableStorage {
    override fun getItem(slot: Int): ItemStack {
        val slot = inventoryStorage.getSlot(slot)
        return slot.resource.toStack(slot.amount.toInt())
    }

}

@WithMinecraft
internal class FabricSlottedStorageTests: SlottedStorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
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

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): SlottedStorage {
        if (secondary)
            return DummySlottedStorage(items.size, items)
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

