package site.siredvin.peripheralium.storages.energy

import site.siredvin.peripheralium.data.LibText

object Energies {
    val EMPTY = EnergyUnit("empty", LibText.EMPTY_ENERGY.text)
    val TURTLE_FUEL = EnergyUnit("turtleFuel", LibText.TURTLE_FUEL_ENERGY.text)
}
