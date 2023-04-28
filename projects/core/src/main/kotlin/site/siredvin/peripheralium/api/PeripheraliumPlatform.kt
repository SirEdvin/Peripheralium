package site.siredvin.peripheralium.api

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item

abstract class PeripheraliumPlatform {

    companion object {
        private var _IMPL: PeripheraliumPlatform? = null

        fun configure(impl: PeripheraliumPlatform) {
            _IMPL = impl
        }

        fun get(): PeripheraliumPlatform {
            if (_IMPL == null)
                throw IllegalStateException("You should init Peripheral Platform first")
            return _IMPL!!
        }

        fun getKey(item: Item): ResourceLocation {
            return get().getKey(item)
        }

        fun getEntityType(key: ResourceLocation): EntityType<Entity> {
            return get().getEntityType(key)
        }
    }
    abstract fun getKey(item: Item): ResourceLocation

    abstract fun getEntityType(key: ResourceLocation): EntityType<Entity>

}