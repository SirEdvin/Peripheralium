package site.siredvin.peripheralium.api.turtle

import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData

interface TurtleUpgradeContainer {
    fun getStoredUpgrades(): List<UpgradeData<ITurtleUpgrade>>
}
