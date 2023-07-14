package site.siredvin.peripheralium.xplat

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

interface PeripheraliumPlatform {

    companion object {
        private var _IMPL: PeripheraliumPlatform? = null

        fun configure(impl: PeripheraliumPlatform) {
            _IMPL = impl
        }

        fun get(): PeripheraliumPlatform {
            if (_IMPL == null) {
                throw IllegalStateException("You should init Peripheral Platform first")
            }
            return _IMPL!!
        }

        val fluidCompactDivider: Int
            get() = get().fluidCompactDivider

        val minecraftServer: MinecraftServer?
            get() = get().minecraftServer

        fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
            return get().wrap(registry)
        }

        fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer {
            return get().createFakePlayer(level, profile)
        }

        fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
            return get().getTurtleAccess(entity)
        }

        fun getPeripheral(level: Level, pos: BlockPos, side: Direction = Direction.NORTH): IPeripheral? {
            return get().getPeripheral(level, pos, side)
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

        fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean = true): Boolean {
            return get().setChunkForceLoad(level, modID, owner, chunkPos, add, ticking)
        }

        fun nbtHash(tag: CompoundTag?): String? {
            return get().nbtHash(tag)
        }

        fun getTurtleUpgrade(stack: ItemStack): UpgradeData<ITurtleUpgrade>? {
            return get().getTurtleUpgrade(stack)
        }

        fun getPocketUpgrade(stack: ItemStack): UpgradeData<IPocketUpgrade>? {
            return get().getPocketUpgrade(stack)
        }

        fun getTurtleUpgrade(key: String): ITurtleUpgrade? {
            return get().getTurtleUpgrade(key)
        }

        fun getPocketUpgrade(key: String): IPocketUpgrade? {
            return get().getPocketUpgrade(key)
        }

        fun nbtToLua(tag: Tag): Any? {
            return get().nbtToLua(tag)
        }

        fun <T : BlockEntity> createBlockEntityType(
            factory: BiFunction<BlockPos, BlockState, T>,
            block: Block,
        ): BlockEntityType<T> {
            return get().createBlockEntityType(factory, block)
        }

        fun <T : Entity> createEntityType(
            name: ResourceLocation,
            factory: Function<Level, T>,
        ): EntityType<T> {
            return get().createEntityType(name, factory)
        }

        fun createTabBuilder(): CreativeModeTab.Builder {
            return get().createTabBuilder()
        }

        fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack> {
            return get().createTurtlesWithUpgrade(upgrade)
        }
        fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack> {
            return get().createPocketsWithUpgrade(upgrade)
        }

        fun isOre(block: BlockState): Boolean {
            return get().isOre(block)
        }

        fun triggerRenderUpdate(blockEntity: BlockEntity) {
            get().triggerRenderUpdate(blockEntity)
        }

        fun tintConvert(tint: Int): Int {
            return get().tintConvert(tint)
        }

        fun reverseTintConvert(tint: Int): Int {
            return get().reverseTintConvert(tint)
        }
    }

    val fluidCompactDivider: Int
    val minecraftServer: MinecraftServer?

    fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T>

    fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer

    fun getTurtleAccess(entity: BlockEntity): ITurtleAccess?

    fun getPeripheral(level: Level, pos: BlockPos, side: Direction): IPeripheral?

    fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean

    fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult

    fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult

    fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean = true): Boolean

    fun nbtHash(tag: CompoundTag?): String?

    fun getTurtleUpgrade(stack: ItemStack): UpgradeData<ITurtleUpgrade>?

    fun getPocketUpgrade(stack: ItemStack): UpgradeData<IPocketUpgrade>?

    fun getTurtleUpgrade(key: String): ITurtleUpgrade?

    fun getPocketUpgrade(key: String): IPocketUpgrade?

    fun nbtToLua(tag: Tag): Any?

    fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block,
    ): BlockEntityType<T>

    fun <T : Entity> createEntityType(
        name: ResourceLocation,
        factory: Function<Level, T>,
    ): EntityType<T>

    fun createTabBuilder(): CreativeModeTab.Builder

    fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack>
    fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack>
    fun isOre(block: BlockState): Boolean
    fun triggerRenderUpdate(blockEntity: BlockEntity)
    fun tintConvert(tint: Int): Int = tint
    fun reverseTintConvert(tint: Int): Int = tintConvert(tint)
}
