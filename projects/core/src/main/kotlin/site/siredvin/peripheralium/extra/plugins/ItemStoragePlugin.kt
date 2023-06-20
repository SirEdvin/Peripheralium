package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.level.Level
import site.siredvin.peripheralium.storages.item.ItemStorage

class ItemStoragePlugin(
    override val storage: ItemStorage,
    override val level: Level,
    override val itemStorageTransferLimit: Int,
) : AbstractItemStoragePlugin()
