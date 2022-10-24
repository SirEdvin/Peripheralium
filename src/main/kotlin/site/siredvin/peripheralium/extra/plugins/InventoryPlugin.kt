package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.shared.util.ItemStorage
import net.minecraft.world.level.Level

class InventoryPlugin(override val level: Level, override val itemStorage: ItemStorage): AbstractInventoryPlugin() {

    /*Kotlin rework from https://github.com/cc-tweaked/cc-restitched/blob/mc-1.18.x%2Fstable/src/main/java/dan200/computercraft/shared/peripheral/generic/methods/InventoryMethods.java */

    companion object {
        const val PLUGIN_TYPE = "inventory"
    }

    override val additionalType: String
        get() = PLUGIN_TYPE
}