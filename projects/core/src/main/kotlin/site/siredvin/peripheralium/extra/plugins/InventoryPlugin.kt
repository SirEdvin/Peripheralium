package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.SlottedStorage

class InventoryPlugin(override val level: Level, override val storage: SlottedStorage): AbstractInventoryPlugin() {
}