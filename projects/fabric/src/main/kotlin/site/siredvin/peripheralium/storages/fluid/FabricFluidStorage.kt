package site.siredvin.peripheralium.storages.fluid

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.function.Predicate
import kotlin.math.roundToLong

class FabricFluidStorage(private val storage: Storage<FluidVariant>) : FluidStorage {

    override val movableType: String
        get() = "fabric"

    override fun getFluids(): Iterator<FluidStack> {
        return storage.nonEmptyViews().map {
            it.toVanilla()
        }.iterator()
    }

    override fun takeFluid(predicate: Predicate<FluidStack>, limit: Int): FluidStack {
        if (!storage.supportsExtraction()) return FluidStack.EMPTY
        val extractableTarget = StorageUtil.findExtractableContent(storage, {
            predicate.test(it.toVanilla())
        }, null)
        if (extractableTarget == null || extractableTarget.amount == 0L) {
            return FluidStack.EMPTY
        }
        val realLimit = minOf(extractableTarget.amount, limit.toLong())
        Transaction.openOuter().use {
            val extracted = storage.extract(extractableTarget.resource, realLimit, it)
            it.commit()
            return extractableTarget.resource.toVanilla(extracted)
        }
    }

    override fun storeFluid(stack: FluidStack): FluidStack {
        if (!storage.supportsInsertion()) return stack
        val fabricAmount = (stack.amount * PeripheraliumPlatform.fluidCompactDivider).roundToLong()
        Transaction.openOuter().use {
            val inserted = storage.insert(stack.toVariant(), fabricAmount, it)
            if (inserted == 0L) {
                it.abort()
                return stack
            }
            it.commit()
            return stack.copyWithCount(((fabricAmount - inserted) / PeripheraliumPlatform.fluidCompactDivider).roundToLong())
        }
    }

    override fun setChanged() {
    }
}
