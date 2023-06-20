package site.siredvin.peripheralium.storages.energy

import net.minecraftforge.energy.IEnergyStorage
import java.util.function.Predicate

class EnergyHandlerWrapper(private val handler: IEnergyStorage) : EnergyStorage {
    override val energy: EnergyStack
        get() = EnergyStack(ForgeEnergies.FORGE, handler.energyStored.toLong())
    override val capacity: Long
        get() = handler.maxEnergyStored.toLong()

    override fun takeEnergy(predicate: Predicate<EnergyStack>, limit: Int): EnergyStack {
        if (!predicate.test(energy)) return EnergyStack.EMPTY
        val extractedEnergy = handler.extractEnergy(limit, false)
        return EnergyStack(ForgeEnergies.FORGE, extractedEnergy.toLong())
    }

    override fun storeEnergy(stack: EnergyStack): EnergyStack {
        if (!stack.`is`(ForgeEnergies.FORGE)) return stack
        val storedEnergy = handler.receiveEnergy(stack.amount.toInt(), false)
        return EnergyStack(ForgeEnergies.FORGE, storedEnergy.toLong())
    }

    override fun setChanged() {
    }
}
