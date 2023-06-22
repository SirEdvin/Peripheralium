package site.siredvin.peripheralium.tests

import net.minecraft.network.chat.Component
import org.junit.jupiter.api.Test
import site.siredvin.peripheralium.storages.energy.*
import java.util.function.Predicate
import kotlin.test.assertEquals

abstract class EnergyStorageTests {
    companion object {
        val DUMMY_ENERGY = EnergyUnit("dummy", Component.literal("dummy"))
    }
    abstract fun createStorage(energy: EnergyStack, capacity: Long, secondary: Boolean): EnergyStorage

    open val defaultUnits: EnergyUnit
        get() = Energies.TURTLE_FUEL

    @Test
    fun testMoveTo() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(EnergyStack(defaultUnits, 500), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(500, moved)
        assertEquals(500, from.energy.amount)
        assertEquals(1000, to.energy.amount)
    }

    @Test
    fun testMoveToFailed() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(EnergyStack(DUMMY_ENERGY, 500), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(0, moved)
        assertEquals(1000, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(500, to.energy.amount)
        assertEquals(DUMMY_ENERGY, to.energy.unit)
    }

    @Test
    fun testMoveToEmpty() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(EnergyStack(Energies.EMPTY, 0), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(1000, moved)
        assertEquals(0, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(1000, to.energy.amount)
        assertEquals(defaultUnits, to.energy.unit)
    }

    @Test
    fun testMoveFrom() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(EnergyStack(defaultUnits, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(500, moved)
        assertEquals(500, from.energy.amount)
        assertEquals(1000, to.energy.amount)
    }

    @Test
    fun testMoveFromFailed() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(EnergyStack(DUMMY_ENERGY, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(0, moved)
        assertEquals(1000, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(500, to.energy.amount)
        assertEquals(DUMMY_ENERGY, to.energy.unit)
    }

    @Test
    fun testMoveFromEmpty() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(EnergyStack(Energies.EMPTY, 0), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(1000, moved)
        assertEquals(0, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(1000, to.energy.amount)
        assertEquals(defaultUnits, to.energy.unit)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(EnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(EnergyStack(Energies.EMPTY, 0), 1000, true)
        val predicate: Predicate<EnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals(1000, movedAmount)
        assertEquals(0, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(1000, to.energy.amount)
        assertEquals(defaultUnits, to.energy.unit)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(EnergyStack(DUMMY_ENERGY, 1000), 1000, false)
        val to = createStorage(EnergyStack(Energies.EMPTY, 0), 1000, true)
        val predicate: Predicate<EnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals(0, movedAmount)
        assertEquals(1000, from.energy.amount)
        assertEquals(DUMMY_ENERGY, from.energy.unit)
        assertEquals(0, to.energy.amount)
        assertEquals(Energies.EMPTY, to.energy.unit)
    }
}
