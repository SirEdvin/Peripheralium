package site.siredvin.peripheralium.tests

import site.siredvin.peripheralium.storage.DummyEnergyStorage
import site.siredvin.peripheralium.storages.energy.*

class ForgeEnergyStorageTests : EnergyStorageTests() {

    override val defaultUnits: EnergyUnit
        get() = ForgeEnergies.FORGE
    override fun createStorage(energy: EnergyStack, capacity: Long, secondary: Boolean): EnergyStorage {
        if (energy.unit != defaultUnits) {
            return DummyEnergyStorage(capacity, energy)
        }
        val baseStorage = net.minecraftforge.energy.EnergyStorage(capacity.toInt())
        baseStorage.receiveEnergy(energy.amount.toInt(), false)
        return EnergyHandlerWrapper(baseStorage)
    }
}

class ForgeDummyEnergyStorageTests : EnergyStorageTests() {

    override val defaultUnits: EnergyUnit
        get() = ForgeEnergies.FORGE
    override fun createStorage(energy: EnergyStack, capacity: Long, secondary: Boolean): EnergyStorage {
        if (energy.unit != defaultUnits || secondary) {
            return DummyEnergyStorage(capacity, energy)
        }
        val baseStorage = net.minecraftforge.energy.EnergyStorage(capacity.toInt())
        baseStorage.receiveEnergy(energy.amount.toInt(), false)
        return EnergyHandlerWrapper(baseStorage)
    }
}
