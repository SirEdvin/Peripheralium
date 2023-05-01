package site.siredvin.peripheralium.storage

import dan200.computercraft.api.lua.LuaException
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.*
import java.util.function.Predicate

class FabricStorageWrapper(val storage: FabricStorage<ItemVariant>): MovableStorage {
    companion object {
        const val MOVABLE_TYPE = "fabricTransaction"
    }

    override fun moveTo(to: TargetableStorage, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        if (to is FabricSlottedStorageWrapper)
            return to.moveFrom(this, limit, toSlot, -1, takePredicate)
        if (to is FabricStorageWrapper) {
            if (toSlot > -1)
                throw LuaException("To slot doesn't support slotting")
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        if (toSlot < -1 && to !is TargetableSlottedStorage)
            throw LuaException("To slot doesn't support slotting")
        return FabricStorageUtils.moveToTargetable(this.storage, to, limit, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: Storage,
        limit: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>
    ): Int {
        if (from is FabricSlottedStorageWrapper) {
            val slotStorage = from.getSingleSlot(fromSlot)
            return StorageUtil.move(slotStorage, storage,  FabricStorageUtils.wrap(takePredicate), limit.toLong(), null).toInt()
        }
        if (from is FabricStorageWrapper) {
            if (fromSlot > -1)
                throw LuaException("From storage doesn't support slotting")
            return StorageUtil.move(from.storage, storage,  FabricStorageUtils.wrap(takePredicate), limit.toLong(), null).toInt()
        }
        return FabricStorageUtils.moveFromTargetable(from, storage, limit, fromSlot, takePredicate)
    }

    override val movableType: String
        get() = MOVABLE_TYPE

    override fun getItems(): Iterator<ItemStack> {
        return SlidingIterator(storage.iterator())
    }

    override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack {
        throw IllegalAccessError("Please, use movable methods for this!")
    }

    override fun storeItem(stack: ItemStack): ItemStack {
        throw IllegalAccessError("Please, use movable methods for this!")
    }

    override fun setChanged() {
    }
}