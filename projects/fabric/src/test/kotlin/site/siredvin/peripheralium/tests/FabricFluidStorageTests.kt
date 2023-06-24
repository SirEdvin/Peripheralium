package site.siredvin.peripheralium.tests

import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import site.siredvin.peripheralium.storage.DummyFluidStorage
import site.siredvin.peripheralium.storages.fluid.FabricFluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.storages.fluid.FluidStorage
import site.siredvin.peripheralium.storages.fluid.toVariant
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

@WithMinecraft
internal class FabricFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage {
        return FabricFluidStorage(
            CombinedStorage(
                fluids.map { stack ->
                    val storage = SingleFluidStorage.withFixedCapacity(1000L * PeripheraliumPlatform.fluidCompactDivider.toLong()) {}
                    if (!stack.isEmpty) {
                        Transaction.openOuter().use {
                            storage.insert(stack.toVariant(), stack.platformAmount, it)
                            it.commit()
                        }
                    }
                    return@map storage
                },
            ),
        )
    }
}

@WithMinecraft
internal class FabricDummyFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage {
        if (secondary) {
            return DummyFluidStorage(fluids.size, fluids)
        }
        return FabricFluidStorage(
            CombinedStorage(
                fluids.map { stack ->
                    val storage = SingleFluidStorage.withFixedCapacity(1000L * PeripheraliumPlatform.fluidCompactDivider.toLong()) {}
                    if (!stack.isEmpty) {
                        Transaction.openOuter().use {
                            storage.insert(stack.toVariant(), stack.platformAmount, it)
                            it.commit()
                        }
                    }
                    return@map storage
                },
            ),
        )
    }
}

@WithMinecraft
internal class FabricReverseDummyFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage {
        if (!secondary) {
            return DummyFluidStorage(fluids.size, fluids)
        }
        return FabricFluidStorage(
            CombinedStorage(
                fluids.map { stack ->
                    val storage = SingleFluidStorage.withFixedCapacity(1000L * PeripheraliumPlatform.fluidCompactDivider.toLong()) {}
                    if (!stack.isEmpty) {
                        Transaction.openOuter().use {
                            storage.insert(stack.toVariant(), stack.platformAmount, it)
                            it.commit()
                        }
                    }
                    return@map storage
                },
            ),
        )
    }
}
