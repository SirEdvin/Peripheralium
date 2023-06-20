package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.level.Level
import site.siredvin.peripheralium.storages.item.SlottedItemStorage

class InventoryPlugin(override val level: Level, override val storage: SlottedItemStorage) : AbstractInventoryPlugin()
