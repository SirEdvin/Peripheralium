package site.siredvin.peripheralium.storages.item

import dan200.computercraft.api.lua.LuaException
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.storages.FabricStorageUtils
import java.util.function.Predicate
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage

open class FabricStorageWrapper(internal val storage: FabricStorage<ItemVariant>) : ItemStorage {

    override fun moveTo(to: ItemSink, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        if (to is FabricSlottedStorageWrapper) {
            return to.moveFrom(this, limit, toSlot, -1, takePredicate)
        }
        if (to is FabricStorageWrapper) {
            if (toSlot > -1) {
                throw LuaException("To slot doesn't support slotting")
            }
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        if (toSlot < -1 && to !is SlottedItemSink) {
            throw LuaException("To slot doesn't support slotting")
        }
        return FabricStorageUtils.moveToTargetable(this.storage, to, limit, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: ItemStorage,
        limit: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>,
    ): Int {
        if (from is FabricSlottedStorageWrapper) {
            if (fromSlot > 0) {
                val slotStorage = from.getSingleSlot(fromSlot)
                return StorageUtil.move(
                    slotStorage,
                    storage,
                    FabricStorageUtils.wrapItem(takePredicate),
                    limit.toLong(),
                    null,
                ).toInt()
            }
            return StorageUtil.move(from.storage, storage, FabricStorageUtils.wrapItem(takePredicate), limit.toLong(), null).toInt()
            // TODO: catch this case with testing!
        }
        if (from is FabricStorageWrapper) {
            if (fromSlot > -1) {
                throw LuaException("From storage doesn't support slotting")
            }
            return StorageUtil.move(from.storage, storage, FabricStorageUtils.wrapItem(takePredicate), limit.toLong(), null).toInt()
        }
        return FabricStorageUtils.moveFromTargetable(from, storage, limit, fromSlot, takePredicate)
    }

    override val movableType: String
        get() = FabricStorageUtils.MOVABLE_TYPE

    override fun getItems(): Iterator<ItemStack> {
        return SlidingIterator(storage.iterator())
    }

    override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack {
        Transaction.openOuter().use {
            val extractionTarget = StorageUtil.findExtractableContent(storage, FabricStorageUtils.wrapItem(predicate), it)
                ?: return ItemStack.EMPTY
            if (extractionTarget.amount == 0L) {
                return ItemStack.EMPTY
            }
            val amount = storage.extract(extractionTarget.resource, limit.toLong(), it)
            if (amount < 1) {
                it.abort()
                return ItemStack.EMPTY
            }
            it.commit()
            return extractionTarget.resource.toStack(amount.toInt())
        }
    }

    override fun storeItem(stack: ItemStack): ItemStack {
        Transaction.openOuter().use {
            val amount = storage.insert(ItemVariant.of(stack), stack.count.toLong(), it).toInt()
            stack.shrink(amount)
            it.commit()
            if (stack.isEmpty) {
                return ItemStack.EMPTY
            }
            return stack
        }
    }

    override fun setChanged() {
    }
}
