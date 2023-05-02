package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.Container
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.TargetableContainer

class InventoryPlugin(override val level: Level, container: Container): AbstractInventoryPlugin() {
    override val storage = TargetableContainer(container)
}