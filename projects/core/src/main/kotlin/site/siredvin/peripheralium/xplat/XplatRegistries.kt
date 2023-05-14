package site.siredvin.peripheralium.xplat

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block


object XplatRegistries {
        val ITEMS by lazy {  PeripheraliumPlatform.wrap(Registries.ITEM) }
        val BLOCKS by lazy { PeripheraliumPlatform.wrap(Registries.BLOCK) }
        val ENTITY_TYPES by lazy { PeripheraliumPlatform.wrap(Registries.ENTITY_TYPE) }
}