package site.siredvin.peripheralium.xplat

import com.mojang.authlib.GameProfile
import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

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

        fun createFakePlayer(level: Level, profile: GameProfile?): ServerPlayer {
            return get().createFakePlayer(level, profile)
        }

        fun getBlockRegistry(): HolderGetter<Block> {
            return get().getBlockRegistry()
        }
    }
    abstract fun getKey(item: Item): ResourceLocation

    abstract fun getEntityType(key: ResourceLocation): EntityType<Entity>

    abstract fun createFakePlayer(level: Level, profile: GameProfile?): ServerPlayer

    abstract fun getBlockRegistry(): HolderGetter<Block>

}