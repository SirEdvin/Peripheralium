package site.siredvin.testiralium.xplat

import net.minecraft.core.registries.Registries

object XplatRegistries {
    val ITEMS by lazy { XplatToolkit.wrap(Registries.ITEM) }
    val BLOCKS by lazy { XplatToolkit.wrap(Registries.BLOCK) }
    val ENTITY_TYPES by lazy { XplatToolkit.wrap(Registries.ENTITY_TYPE) }
    val BLOCK_ENTITY_TYPES by lazy { XplatToolkit.wrap(Registries.BLOCK_ENTITY_TYPE) }
    val MENU by lazy {XplatToolkit.wrap(Registries.MENU)}
}
