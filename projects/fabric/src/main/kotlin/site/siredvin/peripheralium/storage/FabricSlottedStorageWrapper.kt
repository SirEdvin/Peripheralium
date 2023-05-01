package site.siredvin.peripheralium.storage

import dan200.computercraft.api.lua.LuaException
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.*
import java.util.function.Predicate

class FabricSlottedStorageWrapper(private val storage: net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage<ItemVariant>): MovableSlottedStorage {

    override fun moveTo(
        to: TargetableStorage,
        limit: Int,
        fromSlot: Int,
        toSlot: Int,
        takePredicate: Predicate<ItemStack>
    ): Int {
        if (to is FabricSlottedStorageWrapper)
            return to.moveFrom(this, limit, toSlot, fromSlot, takePredicate)
        if (to is FabricStorageWrapper) {
            if (toSlot > -1)
                throw LuaException("To storage doesn't support slotting")
            return to.moveFrom(this, limit, fromSlot, takePredicate)
        }
        return if (fromSlot < 0) {
            FabricStorageUtils.moveToTargetable(storage, to, limit, toSlot, takePredicate)
        } else {
            FabricStorageUtils.moveToTargetable(getSingleSlot(fromSlot), to, limit, toSlot, takePredicate)
        }
    }

    override fun moveFrom(
        from: Storage,
        limit: Int,
        toSlot: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>
    ): Int {
        val operableStorage = if (fromSlot < 0) {
            storage
        } else {
            getSingleSlot(fromSlot)
        }
        if (from is FabricSlottedStorageWrapper) {
            val slotStorage = from.getSingleSlot(fromSlot)
            return StorageUtil.move(slotStorage, operableStorage,  FabricStorageUtils.wrap(takePredicate), limit.toLong(), null).toInt()
        }
        if (from is FabricStorageWrapper) {
            if (fromSlot > -1)
                throw LuaException("From storage doesn't support slotting")
            return StorageUtil.move(from.storage, operableStorage,  FabricStorageUtils.wrap(takePredicate), limit.toLong(), null).toInt()
        }
        return FabricStorageUtils.moveFromTargetable(from, operableStorage, limit, fromSlot, takePredicate)
    }

    override fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack {
        throw IllegalAccessError("Please, use movable methods for this!")
    }

    override fun getItem(slot: Int): ItemStack {
        val slot = storage.getSlot(slot)
        return slot.resource.toStack(slot.amount.toInt())
    }

    fun getSingleSlot(slot: Int): SingleSlotStorage<ItemVariant> {
        return storage.getSlot(slot)
    }

    override fun setItem(slot: Int, item: ItemStack) {
        throw IllegalAccessError("Please, use movable methods for this!")
    }

    override fun canPlaceItem(slot: Int, item: ItemStack): Boolean {
        return true
    }

    override fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack {
        throw IllegalAccessError("Please, use movable methods for this!")
    }

    override fun setChanged() {
    }

    override val size: Int
        get() = storage.slotCount

    override val movableType: String
        get() = FabricStorageWrapper.MOVABLE_TYPE
}