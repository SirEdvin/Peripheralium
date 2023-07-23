package site.siredvin.peripheralium.storages

import dan200.computercraft.api.lua.LuaException
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.storages.fluid.*
import site.siredvin.peripheralium.storages.item.*
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.function.Predicate
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage

object FabricStorageUtils {

    const val MOVABLE_TYPE = "fabricTransaction"

    private class PredicateWrapper(private val predicate: Predicate<ItemStack>) : Predicate<ItemVariant> {
        override fun test(p0: ItemVariant): Boolean {
            return predicate.test(p0.toStack())
        }
    }

    fun wrapItem(predicate: Predicate<ItemStack>): Predicate<ItemVariant> {
        return PredicateWrapper(predicate)
    }

    fun wrapFluid(predicate: Predicate<FluidStack>): Predicate<FluidVariant> {
        return Predicate<FluidVariant> { predicate.test(it.toVanilla()) }
    }

    /**
     * Generic move to any targetable storage, should be used only after make sure that to is not fabric one related!
     */
    fun moveToTargetable(storage: FabricStorage<ItemVariant>, to: ItemSink, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        assert(to.movableType != MOVABLE_TYPE)

        val transaction = Transaction.openOuter()
        transaction.use {
            val resource =
                StorageUtil.findExtractableResource(storage, wrapItem(takePredicate), it)
                    ?: return 0
            val extractedAmount = storage.extract(resource, limit.toLong(), it).toInt()
            if (extractedAmount == 0) {
                return 0
            }
            val insertionStack = resource.toStack(extractedAmount)
            val remainder = if (toSlot < 0) {
                to.storeItem(insertionStack)
            } else {
                (to as SlottedItemSink).storeItem(insertionStack, toSlot, toSlot)
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
    fun moveFromTargetable(from: site.siredvin.peripheralium.storages.item.ItemStorage, to: FabricStorage<ItemVariant>, limit: Int, fromSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        assert(from.movableType != MOVABLE_TYPE)

        val insertionStack = if (fromSlot < 0) {
            from.takeItems(takePredicate, limit)
        } else {
            if (from !is SlottedItemStorage) {
                throw LuaException("From doesn't support slotting")
            }
            from.takeItems(limit, fromSlot, fromSlot, takePredicate)
        }
        if (insertionStack.isEmpty) {
            return 0
        }

        val transaction = Transaction.openOuter()
        transaction.use {
            val insertedAmount = to.insert(ItemVariant.of(insertionStack), insertionStack.count.toLong(), it)

            val remainCount = insertionStack.count - insertedAmount
            if (remainCount > 0) {
                if (fromSlot > -1) {
                    (from as SlottedItemStorage).storeItem(insertionStack.copyWithCount(remainCount.toInt()), fromSlot, fromSlot)
                } else {
                    from.storeItem(insertionStack.copyWithCount(remainCount.toInt()))
                }
            }
            it.commit()
            return insertedAmount.toInt()
        }
    }

    fun moveToTargetable(storage: FabricStorage<FluidVariant>, to: FluidSink, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        assert(to.movableType != MOVABLE_TYPE)

        val platformLimit = limit * PeripheraliumPlatform.fluidCompactDivider

        val transaction = Transaction.openOuter()
        transaction.use {
            val resource =
                StorageUtil.findExtractableResource(storage, wrapFluid(takePredicate), it)
                    ?: return 0
            val extractedAmount = storage.extract(resource, platformLimit, it)
            if (extractedAmount == 0L) {
                return 0L
            }
            val insertionStack = resource.toVanilla(extractedAmount)
            val remainder = to.storeFluid(insertionStack)
            val insertedCount = extractedAmount - remainder.platformAmount
            if (!remainder.isEmpty) {
                storage.insert(resource, remainder.platformAmount, it)
            }
            it.commit()
            return insertedCount / PeripheraliumPlatform.fluidCompactDivider
        }
    }

    fun moveFromTargetable(from: site.siredvin.peripheralium.storages.fluid.FluidStorage, to: FabricStorage<FluidVariant>, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        assert(from.movableType != MOVABLE_TYPE)

        val platformLimit = limit * PeripheraliumPlatform.fluidCompactDivider

        val insertionStack = from.takeFluid(takePredicate, platformLimit)
        if (insertionStack.isEmpty) {
            return 0
        }

        val transaction = Transaction.openOuter()
        transaction.use {
            val insertedAmount = to.insert(insertionStack.toVariant(), insertionStack.platformAmount, it)

            val remainCount = insertionStack.platformAmount - insertedAmount
            if (remainCount > 0) {
                from.storeFluid(insertionStack.copyWithCount(remainCount / PeripheraliumPlatform.fluidCompactDivider))
            }
            it.commit()
            return insertedAmount / PeripheraliumPlatform.fluidCompactDivider
        }
    }

    fun extractStorage(level: Level, pos: BlockPos, @Suppress("UNUSED_PARAMETER") blockEntity: BlockEntity?): site.siredvin.peripheralium.storages.item.ItemStorage? {
        val itemStorage = ItemStorage.SIDED.find(level, pos, null) ?: return null

        return if (itemStorage is net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage) {
            FabricSlottedStorageWrapper(itemStorage)
        } else {
            FabricStorageWrapper(itemStorage)
        }
    }

    fun extractFluidStorage(level: Level, pos: BlockPos, @Suppress("UNUSED_PARAMETER") blockEntity: BlockEntity?): site.siredvin.peripheralium.storages.fluid.FluidStorage? {
        val fluidStorage = FluidStorage.SIDED.find(level, pos, null) ?: return null
        return FabricFluidStorage(fluidStorage)
    }

    fun extractFluidStorageFromItem(@Suppress("UNUSED_PARAMETER") level: Level, stack: ItemStack): site.siredvin.peripheralium.storages.fluid.FluidStorage? {
        val fluidStorage = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack)) ?: return null
        return FabricFluidStorage(fluidStorage)
    }
}
