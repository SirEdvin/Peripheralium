package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

/**
 * Movable storages exists to handle specific conditions for moving items between storage (like Fabric Transactions)
 * Movable storage will always have priority in `moveBetweenStorages` function
 *
 * `movableType` exists to distinguish conditions that requires as to use movable storage
 *
 * If two movableTypes will clash in one `moveBetweenStorages` this will raise error. If two containers with same
 * movableType will be used in `moveBetweenStorages`, `from` container will be prioritized
 */
interface MovableStorage: Storage {
    fun moveTo(to: TargetableStorage, limit: Int, toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int
    fun moveFrom(from: Storage, limit: Int, fromSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int

    val movableType: String
}