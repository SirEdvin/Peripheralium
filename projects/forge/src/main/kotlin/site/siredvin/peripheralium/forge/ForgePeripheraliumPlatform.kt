package site.siredvin.peripheralium.forge

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.impl.Peripherals
import dan200.computercraft.impl.PocketUpgrades
import dan200.computercraft.impl.TurtleUpgrades
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.util.NBTUtil
import net.minecraft.client.Minecraft
import net.minecraft.core.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.world.ForgeChunkManager
import net.minecraftforge.event.level.BlockEvent.BreakEvent
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.RegistryManager
import net.minecraftforge.server.ServerLifecycleHooks
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RegistryWrapper
import site.siredvin.peripheralium.xplat.SavingFunction
import java.awt.Color
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

@Suppress("UnstableApiUsage")
object ForgePeripheraliumPlatform : PeripheraliumPlatform {

    private class ForgeRegistryWrapper<T>(private val name: ResourceLocation, private val registry: ForgeRegistry<T>) : RegistryWrapper<T> {
        override fun getId(something: T): Int {
            val id = registry.getID(something)
            if (id == -1) throw IllegalArgumentException()
            return id
        }

        override fun getKey(something: T): ResourceLocation {
            return registry.getKey(something) ?: throw IllegalArgumentException()
        }

        override fun get(location: ResourceLocation): T {
            return registry.getValue(location) ?: throw IllegalArgumentException()
        }

        override fun get(id: Int): T {
            return registry.getValue(id) ?: throw IllegalArgumentException()
        }

        override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> {
            // TODO: Hm ... this isn't quite right, probably
            return Optional.empty()
        }

        override fun get(resourceKey: ResourceKey<T>): Optional<Holder.Reference<T>> {
            return registry.getDelegate(resourceKey)
        }

        override fun tryGet(location: ResourceLocation): T? {
            return registry.getValue(location)
        }

        override fun iterator(): Iterator<T> {
            return registry.iterator()
        }

        override fun keySet(): Set<ResourceLocation> {
            return registry.keys
        }
    }

    override val fluidCompactDivider: Int
        get() = 1

    override val minecraftServer: MinecraftServer
        get() = ServerLifecycleHooks.getCurrentServer()

    override fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        return ForgeRegistryWrapper(registry.location(), RegistryManager.ACTIVE.getRegistry(registry))
    }

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer {
        return ForgeFakePlayer(level, profile)
    }

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity) {
            return entity.access
        }
        return null
    }

    override fun getPeripheral(level: Level, pos: BlockPos, side: Direction): IPeripheral? {
        return Peripherals.getPeripheral(level, pos, side) {}
    }

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        if (player.server.isUnderSpawnProtection(player.serverLevel(), pos, player)) {
            return true
        }
        val event = BreakEvent(player.level(), pos, state, player)
        MinecraftForge.EVENT_BUS.post(event)
        return event.isCanceled
    }

    override fun interactWithEntity(
        player: ServerPlayer,
        hand: InteractionHand,
        entity: Entity,
        hit: EntityHitResult,
    ): InteractionResult {
        // Copied from CC:T to have nearly same logic here :)
        // Our behaviour is slightly different here - we call onInteractEntityAt before the interact methods, while
        // Forge does the call afterward (on the server, not on the client).
        var interactAt = ForgeHooks.onInteractEntityAt(player, entity, hit.location, hand)
        if (interactAt == null) {
            interactAt = entity.interactAt(player, hit.location.subtract(entity.position()), InteractionHand.MAIN_HAND)
        }

        if (interactAt.consumesAction()) {
            return interactAt
        }

        return player.interactOn(entity, hand)
    }

    override fun useOn(
        player: ServerPlayer,
        stack: ItemStack,
        hit: BlockHitResult,
        canUseBlock: Predicate<BlockState>,
    ): InteractionResult {
        val level = player.level()
        val pos = hit.blockPos
        val event = ForgeHooks.onRightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit)
        if (event.isCanceled) return event.cancellationResult

        val context = UseOnContext(player, InteractionHand.MAIN_HAND, hit)
        if (event.useItem != Event.Result.DENY) {
            val result = stack.onItemUseFirst(context)
            if (result != InteractionResult.PASS) return result
        }

        val block = level.getBlockState(hit.blockPos)
        if (event.useBlock != Event.Result.DENY && !block.isAir && canUseBlock.test(block)) {
            val useResult = block.use(level, player, InteractionHand.MAIN_HAND, hit)
            if (useResult.consumesAction()) return useResult
        }

        return if (event.useItem == Event.Result.DENY) InteractionResult.PASS else stack.useOn(context)
    }

    override fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean): Boolean {
        return ForgeChunkManager.forceChunk(level, modID, owner, chunkPos.x, chunkPos.z, add, ticking)
    }

    override fun nbtHash(tag: CompoundTag?): String? {
        return NBTUtil.getNBTHash(tag)
    }

    override fun getTurtleUpgrade(stack: ItemStack): UpgradeData<ITurtleUpgrade>? {
        return TurtleUpgrades.instance().get(stack)
    }

    override fun getPocketUpgrade(stack: ItemStack): UpgradeData<IPocketUpgrade>? {
        return PocketUpgrades.instance().get(stack)
    }

    override fun getTurtleUpgrade(key: String): ITurtleUpgrade? {
        return TurtleUpgrades.instance().get(key)
    }

    override fun getPocketUpgrade(key: String): IPocketUpgrade? {
        return PocketUpgrades.instance().get(key)
    }

    override fun nbtToLua(tag: Tag): Any? {
        return NBTUtil.toLua(tag)
    }

    override fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block,
    ): BlockEntityType<T> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return BlockEntityType.Builder.of({ t: BlockPos?, u: BlockState? ->
            factory.apply(
                t!!,
                u!!,
            )
        }, block).build(null)
    }

    override fun <T : Entity> createEntityType(
        name: ResourceLocation,
        factory: Function<Level, T>,
    ): EntityType<T> {
        return EntityType.Builder.of({ _, level -> factory.apply(level) }, MobCategory.MISC).build(name.toString())
    }

    override fun createTabBuilder(): CreativeModeTab.Builder {
        return CreativeModeTab.builder()
    }

    override fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack> {
        return listOf(
            ModRegistry.Items.TURTLE_NORMAL.get().create(-1, null, -1, null, upgrade, 0, null),
            ModRegistry.Items.TURTLE_ADVANCED.get().create(-1, null, -1, null, upgrade, 0, null),
        )
    }

    override fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack> {
        return listOf(
            ModRegistry.Items.POCKET_COMPUTER_NORMAL.get().create(-1, null, -1, upgrade),
            ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get().create(-1, null, -1, upgrade),
        )
    }

    override fun triggerRenderUpdate(blockEntity: BlockEntity) {
        val level = blockEntity.level!!
        if (level.isClientSide) {
            val pos = blockEntity.blockPos
            // Basically, just world.setBlocksDirty with bypass model block state check
            Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z)
            blockEntity.requestModelDataUpdate()
        }
    }

    override fun tintConvert(tint: Int): Int {
        // For some unknown reason forge tint should be in bgr
        val color = Color(tint)
        return Color(color.blue, color.green, color.red).rgb
    }

    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
        NetworkHooks.openScreen(player as ServerPlayer, owner, savingFunction::toBytes)
    }
}
