package site.siredvin.peripheralium.api.storage

import dan200.computercraft.api.lua.LuaException
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface TargetableStorage {
    /**
     * Minimal storage abstraction, that can be used to store item in
     * Mostly used for item_storage and inventory plugins
     */
    fun moveFrom(from: Storage, limit: Int, fromSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return StorageUtils.naiveMove(from, this, limit, fromSlot, -1, takePredicate)
        }
        if (fromSlot < 0) {
            return from.moveTo(this, limit, -1, takePredicate)
        }
        if (from !is SlottedStorage) {
            throw LuaException("From storage doesn't support slotting")
        }
        return from.moveTo(this, limit, fromSlot, -1, takePredicate)
    }
    fun storeItem(stack: ItemStack): ItemStack
    fun setChanged()

    val movableType: String?
        get() = null
}
