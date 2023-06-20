package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.storages.item.ItemStorage
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object StorageTestHelpers {
    fun assertNoOverlap(vararg storages: ItemStorage) {
        val stacks = Collections.newSetFromMap(IdentityHashMap<ItemStack, Boolean>())
        for (storage in storages) {
            storage.getItems().forEach {
                if (it != ItemStack.EMPTY) {
                    if (!stacks.add(it)) {
                        throw AssertionError("Duplicate item in inventories")
                    }
                }
            }
        }
    }

    fun assertStorage(storage: ItemStorage, expected: List<Int>, name: String) {
        val notFoundExpected = expected.toMutableList()
        storage.getItems().forEach {
            if (!it.isEmpty) {
                assertTrue(notFoundExpected.remove(it.count), "In $name storage found stack with unexpected count ${it.count}")
            }
        }
        assertTrue(notFoundExpected.isEmpty(), "Cannot find stack with this sizes: $notFoundExpected in $name storage")
    }

    fun assertSlottedStorage(storage: SlottedItemStorage, expected: List<Int>, name: String) {
        expected.forEachIndexed { index, amount ->
            assertEquals(
                amount,
                storage.getItem(index).count,
                "Item in slot $index for $name storage, has incorrect amount",
            )
        }
    }
}
