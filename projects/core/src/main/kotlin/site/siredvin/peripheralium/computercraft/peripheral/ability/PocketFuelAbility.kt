package site.siredvin.peripheralium.computercraft.peripheral.ability

import net.minecraft.world.food.FoodData
import site.siredvin.peripheralium.computercraft.peripheral.owner.PocketPeripheralOwner

class PocketFuelAbility(owner: PocketPeripheralOwner, private val foodFuelPrice: Int, override val maxFuelConsumptionRate: Int) : FuelAbility<PocketPeripheralOwner>(owner) {

    companion object {
        const val MAX_FOOD_LEVEL = 20
    }

    private var fuelConsumptionBuffer = 0

    private fun correctBuffer(foodData: FoodData) {
        if (fuelConsumptionBuffer > foodFuelPrice) {
            val foodToConsume = fuelConsumptionBuffer / foodFuelPrice
            foodData.foodLevel = maxOf(foodData.foodLevel - foodToConsume, 0)
            fuelConsumptionBuffer %= foodFuelPrice
        }
    }

    override fun _consumeFuel(count: Int): Boolean {
        val foodData = owner.owner?.foodData ?: return false
        if (fuelCount < count) {
            return false
        }
        fuelConsumptionBuffer += count
        correctBuffer(foodData)
        return true
    }

    override val isFuelConsumptionDisable: Boolean
        get() = owner.owner?.isCreative ?: true
    override val fuelCount: Int
        get() = (owner.owner?.foodData?.foodLevel ?: 0) * foodFuelPrice - fuelConsumptionBuffer
    override val fuelMaxCount: Int
        get() = if (owner.owner != null) {
            MAX_FOOD_LEVEL * foodFuelPrice
        } else {
            0
        }

    override fun addFuel(count: Int) {
        val foodData = owner.owner?.foodData ?: return
        val foodToAdd = count / foodFuelPrice
        if (foodToAdd > 0) {
            foodData.foodLevel = minOf(MAX_FOOD_LEVEL, foodData.foodLevel + foodToAdd)
        }
        val leftFuel = count % foodFuelPrice
        if (leftFuel > 0) {
            fuelConsumptionBuffer -= leftFuel
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PocketFuelAbility) return false
        if (!super.equals(other)) return false

        if (foodFuelPrice != other.foodFuelPrice) return false
        if (maxFuelConsumptionRate != other.maxFuelConsumptionRate) return false
        if (fuelConsumptionBuffer != other.fuelConsumptionBuffer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + foodFuelPrice
        result = 31 * result + maxFuelConsumptionRate
        result = 31 * result + fuelConsumptionBuffer
        return result
    }
}
