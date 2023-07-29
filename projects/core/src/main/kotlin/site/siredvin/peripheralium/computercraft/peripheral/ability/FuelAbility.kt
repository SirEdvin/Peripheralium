package site.siredvin.peripheralium.computercraft.peripheral.ability

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import site.siredvin.peripheralium.api.peripheral.IOwnerAbility
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin

abstract class FuelAbility<T : IPeripheralOwner>(protected var owner: T) : IOwnerAbility, IPeripheralPlugin {
    protected abstract fun _consumeFuel(count: Int): Boolean
    protected abstract val maxFuelConsumptionRate: Int
    protected fun _getFuelConsumptionRate(): Int {
        val settings = owner.dataStorage
        val rate = settings.getInt(FUEL_CONSUMING_RATE_SETTING)
        if (rate == 0) {
            _setFuelConsumptionRate(DEFAULT_FUEL_CONSUMING_RATE)
            return DEFAULT_FUEL_CONSUMING_RATE
        }
        return rate
    }

    protected fun _setFuelConsumptionRate(raw_rate: Int) {
        var rate = raw_rate
        if (rate < DEFAULT_FUEL_CONSUMING_RATE) rate = DEFAULT_FUEL_CONSUMING_RATE
        val maxFuelRate = maxFuelConsumptionRate
        if (rate > maxFuelRate) rate = maxFuelRate
        owner.dataStorage.putInt(FUEL_CONSUMING_RATE_SETTING, rate)
    }

    abstract val isFuelConsumptionDisable: Boolean
    abstract val fuelCount: Int
    abstract val fuelMaxCount: Int
    abstract fun addFuel(count: Int)
    val fuelConsumptionMultiply: Int
        get() = Math.pow(2.0, (_getFuelConsumptionRate() - 1).toDouble()).toInt()

    fun reduceCooldownAccordingToConsumptionRate(cooldown: Int): Int {
        return cooldown / _getFuelConsumptionRate()
    }

    fun consumeFuel(count: Int, simulate: Boolean): Boolean {
        if (isFuelConsumptionDisable) return true
        val realCount = count * fuelConsumptionMultiply
        return if (simulate) fuelLevel >= realCount else _consumeFuel(realCount)
    }

    @get:LuaFunction(mainThread = true)
    val fuelLevel: Int
        get() = fuelCount

    @get:LuaFunction(mainThread = true)
    val maxFuelLevel: Int
        get() = fuelMaxCount

    @get:LuaFunction(mainThread = true)
    val fuelConsumptionRate: Int
        get() = _getFuelConsumptionRate()

    @LuaFunction(mainThread = true)
    fun setFuelConsumptionRate(rate: Int): MethodResult {
        if (rate < 1) return MethodResult.of(null, "Too small fuel consumption rate")
        if (rate > maxFuelConsumptionRate) return MethodResult.of(null, "Too big fuel consumption rate")
        _setFuelConsumptionRate(rate)
        return MethodResult.of(true)
    }

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["maxFuelConsumptionRate"] = maxFuelConsumptionRate
        data["isFuelConsumptionDisable"] = isFuelConsumptionDisable
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FuelAbility<*>) return false

        if (maxFuelConsumptionRate != other.maxFuelConsumptionRate) return false
        if (isFuelConsumptionDisable != other.isFuelConsumptionDisable) return false
        if (fuelCount != other.fuelCount) return false
        if (fuelMaxCount != other.fuelMaxCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maxFuelConsumptionRate
        result = 31 * result + isFuelConsumptionDisable.hashCode()
        result = 31 * result + fuelCount
        result = 31 * result + fuelMaxCount
        return result
    }

    companion object {
        protected const val FUEL_CONSUMING_RATE_SETTING = "FUEL_CONSUMING_RATE"
        protected const val DEFAULT_FUEL_CONSUMING_RATE = 1
    }
}
