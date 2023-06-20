package site.siredvin.peripheralium.storages.item

import dan200.computercraft.api.lua.LuaException
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface SlottedItemStorage : ItemStorage, SlottedItemSink, AccessibleItemStorage {
    fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack

    fun canPlaceItem(slot: Int, item: ItemStack): Boolean

    override fun getItems(): Iterator<ItemStack> {
        return SlottedStorageIterator(this)
    }

    override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack {
        return takeItems(limit, 0, size - 1, predicate)
    }

    fun moveTo(to: ItemSink, limit: Int, fromSlot: Int = -1, toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return ItemStorageUtils.naiveMove(this, to, limit, fromSlot, toSlot, takePredicate)
        }
        if (toSlot < 0) {
            return to.moveFrom(this, limit, fromSlot, takePredicate)
        }
        if (to !is SlottedItemSink) {
            throw LuaException("To storage doesn't support slotting")
        }
        return to.moveFrom(this, limit, toSlot, fromSlot, takePredicate)
    }

    override fun moveTo(to: ItemSink, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        return moveTo(to, limit, -1, toSlot, takePredicate)
    }
}
