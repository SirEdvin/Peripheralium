package site.siredvin.peripheralium.extra.plugins

import net.minecraft.world.Container
import net.minecraft.world.level.Level

class InventoryPlugin(override val level: Level, override val container: Container): AbstractInventoryPlugin() {

    companion object {
        const val PLUGIN_TYPE = "inventory"
    }

    override val additionalType: String
        get() = PLUGIN_TYPE
}