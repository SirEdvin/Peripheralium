package site.siredvin.peripheralium.xplat

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block


object XplatRegistries {
        val ITEMS: RegistryWrapper<Item> = PeripheraliumPlatform.wrap(Registries.ITEM)
        val BLOCKS: RegistryWrapper<Block> = PeripheraliumPlatform.wrap(Registries.BLOCK)
        val ENTITY_TYPES: RegistryWrapper<EntityType<*>> = PeripheraliumPlatform.wrap(Registries.ENTITY_TYPE)
}