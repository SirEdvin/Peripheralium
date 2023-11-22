package site.siredvin.peripheralium.storages.fluid

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import site.siredvin.peripheralium.storages.FabricStorageUtils
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.function.Predicate

open class FabricFluidStorage(private val storage: Storage<FluidVariant>) : FluidStorage {

    override val movableType: String
        get() = FabricStorageUtils.MOVABLE_TYPE

    override fun getFluids(): Iterator<FluidStack> {
        return this.storage.map { it.toVanilla() }.iterator()
    }

    override fun moveTo(to: FluidSink, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        if (to.movableType == FabricStorageUtils.MOVABLE_TYPE) {
            return to.moveFrom(this, limit, takePredicate)
        }
        if (to.movableType == null) {
            return FabricStorageUtils.moveToTargetable(this.storage, to, limit, takePredicate)
        }
        throw IllegalStateException("Cannot mix movable type, this should be impossible here")
    }

    override fun moveFrom(from: FluidStorage, limit: Long, takePredicate: Predicate<FluidStack>): Long {
        if (from.movableType == FabricStorageUtils.MOVABLE_TYPE) {
            if (from !is FabricFluidStorage) throw IllegalStateException("For fabricTransfer please use FabricFluidStorage")
            return StorageUtil.move(
                from.storage,
                storage,
                { takePredicate.test(it.toVanilla(1)) },
                limit * PeripheraliumPlatform.fluidCompactDivider,
                null,
            ) / PeripheraliumPlatform.fluidCompactDivider
        }
        if (from.movableType == null) {
            return FabricStorageUtils.moveFromTargetable(from, this.storage, limit, takePredicate)
        }
        throw IllegalStateException("Cannot mix movable type, this should be impossible here")
    }

    override fun takeFluid(predicate: Predicate<FluidStack>, limit: Long): FluidStack {
        val platformLimit = limit * PeripheraliumPlatform.fluidCompactDivider
        if (!storage.supportsExtraction()) return FluidStack.EMPTY
        val extractableTarget = StorageUtil.findExtractableContent(storage, {
            predicate.test(it.toVanilla())
        }, null)
        if (extractableTarget == null || extractableTarget.amount == 0L) {
            return FluidStack.EMPTY
        }
        val realLimit = minOf(extractableTarget.amount, platformLimit)
        Transaction.openOuter().use {
            val extracted = storage.extract(extractableTarget.resource, realLimit, it)
            it.commit()
            return extractableTarget.resource.toVanilla(extracted)
        }
    }

    override fun storeFluid(stack: FluidStack): FluidStack {
        if (!storage.supportsInsertion()) return stack
        Transaction.openOuter().use {
            val inserted = storage.insert(stack.toVariant(), stack.platformAmount, it)
            if (inserted == 0L) {
                it.abort()
                return stack
            }
            it.commit()
            return stack.copyWithCount((stack.platformAmount - inserted) / PeripheraliumPlatform.fluidCompactDivider)
        }
    }

    override fun setChanged() {
    }
}
