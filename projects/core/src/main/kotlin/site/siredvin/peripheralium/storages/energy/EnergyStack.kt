package site.siredvin.peripheralium.storages.energy

data class EnergyStack(val unit: EnergyUnit, var amount: Long) {
    companion object {
        val EMPTY = EnergyStack(Energies.EMPTY, 0)
        fun isSameEnergy(first: EnergyStack, second: EnergyStack): Boolean {
            return first.unit == second.unit
        }
    }
    val isEmpty: Boolean
        get() = unit == Energies.EMPTY

    fun copy(): EnergyStack {
        return EnergyStack(unit, amount)
    }

    fun `is`(unit: EnergyUnit): Boolean {
        return unit == this.unit
    }

    fun copyWithCount(count: Long): EnergyStack {
        return EnergyStack(unit, count)
    }

    fun grow(amount: Int) {
        this.amount += amount.toLong()
    }

    fun shrink(amount: Int) {
        this.amount -= amount.toLong()
    }

    fun grow(amount: Long) {
        this.amount += amount
    }

    fun shrink(amount: Long) {
        this.amount -= amount
    }

    fun split(amount: Long): EnergyStack {
        if (this.amount <= amount) {
            val fullStack = this.copy()
            this.amount = 0
            return fullStack
        }
        this.shrink(amount)
        return this.copyWithCount(amount)
    }
}
