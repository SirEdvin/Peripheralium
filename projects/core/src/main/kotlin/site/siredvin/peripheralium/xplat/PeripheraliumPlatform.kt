package site.siredvin.peripheralium.xplat

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.turtle.ITurtleAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import java.util.function.Supplier

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

        fun <T: Item> registerItem(key: ResourceLocation, item: T): Supplier<T> {
            return get().registerItem(key, item)
        }

        fun <T: Block> registerBlock(key: ResourceLocation, block: T, itemFactory: (Block) -> (Item)): Supplier<T> {
            return get().registerBlock(key, block, itemFactory)
        }

        fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
            return get().getTurtleAccess(entity)
        }

        fun isBlockBreakable(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
            return get().isBlockBreakable(pos, state, player)
        }
    }
    abstract fun getKey(item: Item): ResourceLocation

    abstract fun getEntityType(key: ResourceLocation): EntityType<Entity>

    abstract fun createFakePlayer(level: Level, profile: GameProfile?): ServerPlayer

    abstract fun getBlockRegistry(): HolderGetter<Block>

    abstract fun <T: Item> registerItem(key: ResourceLocation, item: T): Supplier<T>

    abstract fun <T: Block> registerBlock(key: ResourceLocation, block: T, itemFactory: (Block) -> (Item)): Supplier<T>

    abstract fun getTurtleAccess(entity: BlockEntity): ITurtleAccess?

    abstract fun isBlockBreakable(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean

}