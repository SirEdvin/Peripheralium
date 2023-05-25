package site.siredvin.peripheralium.api.storage

import dan200.computercraft.api.lua.LuaException
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface Storage : TargetableStorage {
    fun getItems(): Iterator<ItemStack>
    fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack

    fun moveTo(to: TargetableStorage, limit: Int, toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return StorageUtils.naiveMove(this, to, limit, -1, toSlot, takePredicate)
        }
        if (toSlot < 0) {
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        if (to !is TargetableSlottedStorage) {
            throw LuaException("To storage doesn't support slotting")
        }
        return to.moveFrom(this, limit, toSlot, -1, takePredicate)
    }
}
