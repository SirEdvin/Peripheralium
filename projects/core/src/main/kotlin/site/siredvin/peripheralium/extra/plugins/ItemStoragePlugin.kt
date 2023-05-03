package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.Storage

class ItemStoragePlugin(override val storage: Storage, override val level: Level) : AbstractItemStoragePlugin() {
}