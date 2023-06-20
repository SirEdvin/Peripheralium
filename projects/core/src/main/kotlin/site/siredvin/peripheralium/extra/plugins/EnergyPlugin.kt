package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.storages.energy.EnergyStorage

class EnergyPlugin(private val storage: EnergyStorage) : IPeripheralPlugin {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ENERGY_STORAGE

    @LuaFunction(mainThread = true)
    fun getEnergy(): Int {
        return storage.energy.amount.toInt()
    }

    @LuaFunction(mainThread = true)
    fun getEnergyCapacity(): Int {
        return storage.capacity.toInt()
    }

    @LuaFunction(mainThread = true)
    fun getEnergyUnit(): String {
        return storage.energy.unit.name
    }
}
