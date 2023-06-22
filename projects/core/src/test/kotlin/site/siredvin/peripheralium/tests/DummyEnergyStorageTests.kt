package site.siredvin.peripheralium.tests

import site.siredvin.peripheralium.storage.DummyEnergyStorage
import site.siredvin.peripheralium.storages.energy.EnergyStack
import site.siredvin.peripheralium.storages.energy.EnergyStorage

@WithMinecraft
class DummyEnergyStorageTests : EnergyStorageTests() {
    override fun createStorage(energy: EnergyStack, capacity: Long, secondary: Boolean): EnergyStorage {
        return DummyEnergyStorage(capacity, energy)
    }
}
