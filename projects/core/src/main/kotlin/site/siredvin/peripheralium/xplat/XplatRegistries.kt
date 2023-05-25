package site.siredvin.peripheralium.xplat

import net.minecraft.core.registries.Registries

object XplatRegistries {
    val ITEMS by lazy { PeripheraliumPlatform.wrap(Registries.ITEM) }
    val BLOCKS by lazy { PeripheraliumPlatform.wrap(Registries.BLOCK) }
    val ENTITY_TYPES by lazy { PeripheraliumPlatform.wrap(Registries.ENTITY_TYPE) }
}
