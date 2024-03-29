package site.siredvin.peripheralium.storages.item

import dan200.computercraft.api.lua.LuaException
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface SlottedItemSink : ItemSink {
    val size: Int

    fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack

    fun storeItem(stack: ItemStack, startSlot: Int): ItemStack {
        return storeItem(stack, startSlot, size - 1)
    }
    override fun storeItem(stack: ItemStack): ItemStack {
        return storeItem(stack, 0, size - 1)
    }

    fun moveFrom(from: ItemStorage, limit: Int, toSlot: Int = -1, fromSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return ItemStorageUtils.naiveMove(from, this, limit, fromSlot, toSlot, takePredicate)
        }
        if (fromSlot < 0) {
            return from.moveTo(this, limit, toSlot, takePredicate)
        }
        if (from !is SlottedItemStorage) {
            throw LuaException("From storage doesn't support slotting")
        }
        return from.moveTo(this, limit, fromSlot, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: ItemStorage,
        limit: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>,
    ): Int {
        return moveFrom(from, limit, -1, fromSlot, takePredicate)
    }
}
