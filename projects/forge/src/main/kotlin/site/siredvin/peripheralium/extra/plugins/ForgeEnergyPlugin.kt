package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaFunction
import net.minecraftforge.energy.IEnergyStorage
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin

class ForgeEnergyPlugin(private val storage: IEnergyStorage) : IPeripheralPlugin {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ENERGY_STORAGE

    @LuaFunction(mainThread = true)
    fun getEnergy(): Int {
        return storage.energyStored
    }

    @LuaFunction(mainThread = true)
    fun getEnergyCapacity(): Int {
        return storage.maxEnergyStored
    }

    @LuaFunction(mainThread = true)
    fun getEnergyUnit(): String {
        return "RF"
    }
}
