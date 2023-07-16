package site.siredvin.peripheralium.api.pocket

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.upgrades.UpgradeData

interface PocketUpgradeHolder {
    fun getInternalUpgrades(): List<UpgradeData<IPocketUpgrade>>
}
