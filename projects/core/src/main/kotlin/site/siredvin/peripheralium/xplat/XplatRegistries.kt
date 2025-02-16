package site.siredvin.peripheralium.xplat

import net.minecraft.core.Registry

object XplatRegistries {
    val ITEMS by lazy { PeripheraliumPlatform.wrap(Registry.ITEM_REGISTRY) }
    val BLOCKS by lazy { PeripheraliumPlatform.wrap(Registry.BLOCK_REGISTRY) }
    val FLUIDS by lazy { PeripheraliumPlatform.wrap(Registry.FLUID_REGISTRY) }
    val ENTITY_TYPES by lazy { PeripheraliumPlatform.wrap(Registry.ENTITY_TYPE_REGISTRY) }
    val RECIPE_TYPES by lazy { PeripheraliumPlatform.wrap(Registry.RECIPE_TYPE_REGISTRY) }
}
