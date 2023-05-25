package site.siredvin.peripheralium.storage

import dan200.computercraft.api.lua.LuaException
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.storage.*
import java.util.function.Predicate

class FabricSlottedStorageWrapper(internal val storage: net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage<ItemVariant>) : SlottedStorage {

    override fun moveTo(
        to: TargetableStorage,
        limit: Int,
        fromSlot: Int,
        toSlot: Int,
        takePredicate: Predicate<ItemStack>,
    ): Int {
        if (to is FabricSlottedStorageWrapper) {
            return to.moveFrom(this, limit, toSlot, fromSlot, takePredicate)
        }
        if (to is FabricStorageWrapper) {
            if (toSlot > -1) {
                throw LuaException("To storage doesn't support slotting")
            }
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
        takePredicate: Predicate<ItemStack>,
    ): Int {
        val operableStorage = if (toSlot < 0) {
            storage
        } else {
            getSingleSlot(toSlot)
        }
        if (from is FabricSlottedStorageWrapper) {
            if (fromSlot > 0) {
                val slotStorage = from.getSingleSlot(fromSlot)
                return StorageUtil.move(
                    slotStorage,
                    operableStorage,
                    FabricStorageUtils.wrap(takePredicate),
                    limit.toLong(),
                    null,
                ).toInt()
            } else {
                return StorageUtil.move(
                    from.storage,
                    operableStorage,
                    FabricStorageUtils.wrap(takePredicate),
                    limit.toLong(),
                    null,
                ).toInt()
            }
        }
        if (from is FabricStorageWrapper) {
            if (fromSlot > -1) {
                throw LuaException("From storage doesn't support slotting")
            }
            return StorageUtil.move(from.storage, operableStorage, FabricStorageUtils.wrap(takePredicate), limit.toLong(), null).toInt()
        }
        return FabricStorageUtils.moveFromTargetable(from, operableStorage, limit, fromSlot, takePredicate)
    }

    override fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack {
        var slidingStack = ItemStack.EMPTY
        var slidingLimit = limit
        Transaction.openOuter().use {
            for (currentSlot in startSlot..endSlot) {
                val slotStorage = getSingleSlot(currentSlot)
                val extractionTarget = StorageUtil.findExtractableContent(slotStorage, FabricStorageUtils.wrap(predicate), it)
                    ?: continue
                if (extractionTarget.amount == 0L) {
                    continue
                }
                val potentialStack = extractionTarget.resource.toStack(extractionTarget.amount.toInt())
                if (slidingStack.isEmpty || StorageUtils.canMerge(slidingStack, potentialStack, slidingLimit)) {
                    val extractedAmount = slotStorage.extract(extractionTarget.resource, limit.toLong(), it).toInt()
                    if (extractedAmount < 1) {
                        continue
                    }
                    val extractedStack = extractionTarget.resource.toStack(extractedAmount)
                    if (slidingStack.isEmpty) {
                        slidingStack = extractedStack
                        slidingLimit = minOf(slidingLimit, slidingStack.maxStackSize) - slidingStack.count
                    } else {
                        val extractedCount = extractedStack.count
                        val remainder = StorageUtils.inplaceMerge(slidingStack, extractedStack)
                        if (!remainder.isEmpty) {
                            slotStorage.insert(ItemVariant.of(remainder), remainder.count.toLong(), it)
                        }
                        slidingLimit -= extractedCount - remainder.count
                    }
                }
                if (slidingLimit <= 0) {
                    break
                }
            }
            it.commit()
            return slidingStack
        }
    }

    override fun getItem(slot: Int): ItemStack {
        val slot = storage.getSlot(slot)
        return slot.resource.toStack(slot.amount.toInt())
    }

    fun getSingleSlot(slot: Int): SingleSlotStorage<ItemVariant> {
        return storage.getSlot(slot)
    }

    override fun canPlaceItem(slot: Int, item: ItemStack): Boolean {
        return true
    }

    override fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack {
        Transaction.openOuter().use {
            for (currentSlot in startSlot..endSlot) {
                val storageSlot = getSingleSlot(currentSlot)
                val insertedAmount = storageSlot.insert(ItemVariant.of(stack), stack.count.toLong(), it).toInt()
                stack.shrink(insertedAmount)
                if (stack.isEmpty) {
                    it.commit()
                    return ItemStack.EMPTY
                }
            }
            it.commit()
            return stack
        }
    }

    override fun setChanged() {
    }

    override val size: Int
        get() = storage.slotCount

    override val movableType: String
        get() = FabricStorageUtils.MOVABLE_TYPE
}
