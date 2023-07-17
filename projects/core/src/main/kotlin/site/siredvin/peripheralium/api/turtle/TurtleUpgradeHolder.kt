package site.siredvin.peripheralium.api.turtle

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.upgrades.UpgradeData

interface TurtleUpgradeHolder {
    fun getInternalUpgrades(turtle: ITurtleAccess, side: TurtleSide): List<UpgradeData<ITurtleUpgrade>>
}
