package site.siredvin.peripheralium.tests

import site.siredvin.peripheralium.storage.DummyFluidStorage
import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.storages.fluid.FluidStorage

@WithMinecraft
internal class DummyFluidStorageTests : FluidStorageTests() {
    override fun createStorage(fluids: List<FluidStack>, secondary: Boolean): FluidStorage {
        return DummyFluidStorage(fluids.size, fluids)
    }
}
