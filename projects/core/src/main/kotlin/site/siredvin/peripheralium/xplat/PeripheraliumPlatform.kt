package site.siredvin.peripheralium.xplat

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import java.util.function.Predicate
import java.util.function.Supplier


interface PeripheraliumPlatform {

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

        val fluidCompactDivider: Double
            get() = get().fluidCompactDivider

        fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
            return get().wrap(registry)
        }

        fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer {
            return get().createFakePlayer(level, profile)
        }

        fun <T: Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
            return get().registerItem(key, item)
        }

        fun <T: Item> registerItem(name: String, item: Supplier<T>): Supplier<T> {
            return registerItem(ResourceLocation(PeripheraliumCore.MOD_ID, name), item)
        }

        fun <T: Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T> {
            return get().registerBlock(key, block, itemFactory)
        }

        fun <T: Block> registerBlock(name: String, block: Supplier<T>, itemFactory: (T) -> (Item) = { block -> DescriptiveBlockItem(block, Item.Properties()) }): Supplier<T> {
            return get().registerBlock(ResourceLocation(PeripheraliumCore.MOD_ID, name), block, itemFactory)
        }

        fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
            return get().getTurtleAccess(entity)
        }

        fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
            return get().isBlockProtected(pos, state, player)
        }

        fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult {
            return get().interactWithEntity(player, hand, entity, hit)
        }

        fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult {
            return get().useOn(player, stack, hit, canUseBlock)
        }

        fun nbtHash(tag: CompoundTag?): String? {
            return get().nbtHash(tag)
        }

        fun getTurtleUpgrade(stack: ItemStack): ITurtleUpgrade? {
            return get().getTurtleUpgrade(stack)
        }

        fun getPocketUpgrade(stack: ItemStack): IPocketUpgrade? {
            return get().getPocketUpgrade(stack)
        }

        fun getTurtleUpgrade(key: String): ITurtleUpgrade? {
            return get().getTurtleUpgrade(key)
        }

        fun getPocketUpgrade(key: String): IPocketUpgrade? {
            return get().getPocketUpgrade(key)
        }
    }

    val fluidCompactDivider: Double

    fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T>

    fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer

    fun <T: Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T>

    fun <T: Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T>

    fun getTurtleAccess(entity: BlockEntity): ITurtleAccess?

    fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean

    fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult

    fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult

    fun nbtHash(tag: CompoundTag?): String?

    fun getTurtleUpgrade(stack: ItemStack): ITurtleUpgrade?

    fun getPocketUpgrade(stack: ItemStack): IPocketUpgrade?

    fun getTurtleUpgrade(key: String): ITurtleUpgrade?

    fun getPocketUpgrade(key: String): IPocketUpgrade?

}