package site.siredvin.peripheralium.api.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade

interface PocketUpgradeHolder {
    fun getInternalUpgrades(pocket: IPocketAccess): List<IPocketUpgrade>
}
