package site.siredvin.peripheralium.tests

import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.Storage
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object StorageTestHelpers {
    fun assertNoOverlap(vararg storages: Storage) {
        val stacks = Collections.newSetFromMap(IdentityHashMap<ItemStack, Boolean>())
        for (storage in storages) {
            storage.getItems().forEach {
                if (it != ItemStack.EMPTY)
                    if (!stacks.add(it))
                        throw AssertionError("Duplicate item in inventories")
            }
        }
    }

    fun assertStorage(storage: Storage, expected: List<Int>, name: String) {
        val notFoundExpected = expected.toMutableList()
        storage.getItems().forEach {
            if (!it.isEmpty) {
                assertTrue(notFoundExpected.remove(it.count), "In $name storage found stack with unexpected count ${it.count}")
            }
        }
        assertTrue(notFoundExpected.isEmpty(), "Cannot find stack with this sizes: $notFoundExpected in $name storage")
    }

    fun assertSlottedStorage(storage: SlottedStorage, expected: List<Int>, name: String) {
        expected.forEachIndexed { index, amount ->
            assertEquals(
                amount, storage.getItem(index).count,
                "Item in slot $index for $name storage, has incorrect amount"
            )
        }
    }
}