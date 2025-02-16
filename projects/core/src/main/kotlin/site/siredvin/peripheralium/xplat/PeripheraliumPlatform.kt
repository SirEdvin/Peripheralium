package site.siredvin.peripheralium.xplat

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
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
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
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
        private var impl: PeripheraliumPlatform? = null
        private var genericLookupRegistered = false

        fun configure(impl: PeripheraliumPlatform) {
            this.impl = impl
        }

        fun get(): PeripheraliumPlatform {
            if (impl == null) {
                throw IllegalStateException("You should init Peripheral Platform first")
            }
            return impl!!
        }

        val fluidCompactDivider: Int
            get() = get().fluidCompactDivider

        val minecraftServer: MinecraftServer?
            get() = get().minecraftServer

        fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> = get().wrap(registry)

        fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer = get().createFakePlayer(level, profile)

        fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? = get().getTurtleAccess(entity)

        fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction = Direction.NORTH): IPeripheral? = get().getPeripheral(level, pos, side)

        fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean = get().isBlockProtected(pos, state, player)

        fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult = get().interactWithEntity(player, hand, entity, hit)

        fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult = get().useOn(player, stack, hit, canUseBlock)

        fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean = true): Boolean = get().setChunkForceLoad(level, modID, owner, chunkPos, add, ticking)

        fun nbtHash(tag: CompoundTag?): String? = get().nbtHash(tag)

        fun getTurtleUpgrade(stack: ItemStack): ITurtleUpgrade? = get().getTurtleUpgrade(stack)

        fun getPocketUpgrade(stack: ItemStack): IPocketUpgrade? = get().getPocketUpgrade(stack)

        fun getTurtleUpgrade(key: String): ITurtleUpgrade? = get().getTurtleUpgrade(key)

        fun getPocketUpgrade(key: String): IPocketUpgrade? = get().getPocketUpgrade(key)

        fun nbtToLua(tag: Tag): Any? = get().nbtToLua(tag)

        fun <T : BlockEntity> createBlockEntityType(
            factory: BiFunction<BlockPos, BlockState, T>,
            block: Block,
        ): BlockEntityType<T> = get().createBlockEntityType(factory, block)

        fun <T : Entity> createEntityType(
            name: ResourceLocation,
            factory: Function<Level, T>,
        ): EntityType<T> = get().createEntityType(name, factory)

        fun createTurtlesWithUpgrade(upgrade: ITurtleUpgrade): List<ItemStack> = get().createTurtlesWithUpgrade(upgrade)
        fun createPocketsWithUpgrade(upgrade: IPocketUpgrade): List<ItemStack> = get().createPocketsWithUpgrade(upgrade)

        fun triggerRenderUpdate(blockEntity: BlockEntity) {
            get().triggerRenderUpdate(blockEntity)
        }

        fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) = get().openMenu(player, owner, savingFunction)

        fun registerGenericPeripheralLookup() {
            if (!genericLookupRegistered) {
                get().registerGenericPeripheralLookup()
                genericLookupRegistered = true
            }
        }
    }

    val fluidCompactDivider: Int
    val minecraftServer: MinecraftServer?

    fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T>

    fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer

    fun getTurtleAccess(entity: BlockEntity): ITurtleAccess?

    fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral?

    fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean

    fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult

    fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult

    fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean = true): Boolean

    fun nbtHash(tag: CompoundTag?): String?

    fun getTurtleUpgrade(stack: ItemStack): ITurtleUpgrade?

    fun getPocketUpgrade(stack: ItemStack): IPocketUpgrade?

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

    fun createTurtlesWithUpgrade(upgrade: ITurtleUpgrade): List<ItemStack>
    fun createPocketsWithUpgrade(upgrade: IPocketUpgrade): List<ItemStack>

    fun triggerRenderUpdate(blockEntity: BlockEntity)

    fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction)

    fun registerGenericPeripheralLookup()
}
