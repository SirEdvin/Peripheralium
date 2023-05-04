package site.siredvin.peripheralium.storage

import com.google.common.collect.Iterators
import dan200.computercraft.api.lua.LuaException
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.api.storage.*
import java.util.function.Predicate
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage

object FabricStorageUtils {
    private class PredicateWrapper(private val predicate: Predicate<ItemStack>): Predicate<ItemVariant> {
        override fun test(p0: ItemVariant): Boolean {
            return predicate.test(p0.toStack())
        }

    }

    fun wrap(predicate: Predicate<ItemStack>): Predicate<ItemVariant> {
        return PredicateWrapper(predicate)
    }

    /**
     * Generic move to any targetable storage, should be used only after make sure that to is not fabric one related!
     */
    fun moveToTargetable(storage: FabricStorage<ItemVariant>, to: TargetableStorage, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        assert(to !is FabricStorageWrapper)

        val transaction = Transaction.openOuter()
        transaction.use {
            val resource =
                StorageUtil.findExtractableResource(storage, wrap(takePredicate), it)
                    ?: return 0
            val extractedAmount = storage.extract(resource, limit.toLong(), it).toInt()
            if (extractedAmount == 0)
                return 0
            val insertionStack = resource.toStack(extractedAmount)
            val remainder = if (toSlot < 0) {
                to.storeItem(insertionStack)
            } else {
                (to as TargetableSlottedStorage).storeItem(insertionStack, toSlot, toSlot)
            }
            val insertedCount = extractedAmount - remainder.count
            if (!remainder.isEmpty) {
                storage.insert(resource, remainder.count.toLong(), it)
            }
            it.commit()
            return insertedCount
        }
    }

    /**
     * Generic move from any targetable storage, should be used only after make sure that to is not fabric one related!
     */
    fun moveFromTargetable(from: Storage, to: FabricStorage<ItemVariant>, limit: Int, fromSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        assert(from !is FabricStorageWrapper)

        val insertionStack = if (fromSlot < 0) {
            from.takeItems(takePredicate, limit)
        } else {
            if (from !is SlottedStorage)
                throw LuaException("From doesn't support slotting")
            from.takeItems(limit, fromSlot, fromSlot, takePredicate)
        }
        if (insertionStack.isEmpty)
            return 0

        val transaction = Transaction.openOuter()
        transaction.use {
            val insertedAmount = to.insert(ItemVariant.of(insertionStack), insertionStack.count.toLong(), it)

            val remainCount = insertionStack.count - insertedAmount
            if (remainCount > 0) {
                if (fromSlot > -1) {
                    (from as SlottedStorage).storeItem(insertionStack.copyWithCount(remainCount.toInt()), fromSlot, fromSlot)
                } else {
                    from.storeItem(insertionStack.copyWithCount(remainCount.toInt()))
                }
            }
            it.commit()
            return insertedAmount.toInt()
        }
    }

    fun extractStorage(level: Level, obj: Any?): Storage? {
        val itemStorage = when (obj) {
            is BlockPos -> {
                ItemStorage.SIDED.find(level, obj, null)
            }

            is BlockEntity -> {
                ItemStorage.SIDED.find(level, obj.blockPos, null)
            }

            else -> {
                null
            }
        } ?: return null

        val size = Iterators.size(itemStorage.iterator())
        if (size == 0)
            return null

        return if (itemStorage is net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage) {
            FabricSlottedStorageWrapper(itemStorage)
        } else {
            FabricStorageWrapper(itemStorage)
        }
    }
}