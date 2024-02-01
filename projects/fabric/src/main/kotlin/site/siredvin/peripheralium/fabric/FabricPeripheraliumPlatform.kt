package site.siredvin.peripheralium.fabric

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.peripheral.PeripheralLookup
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.impl.PocketUpgrades
import dan200.computercraft.impl.TurtleUpgrades
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.util.NBTUtil
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.client.Minecraft
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
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
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
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
import site.siredvin.peripheralium.api.peripheral.IPeripheralProvider
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RegistryWrapper
import site.siredvin.peripheralium.xplat.SavingFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

object FabricPeripheraliumPlatform : PeripheraliumPlatform {
    const val FORGE_COMPACT_DEVIDER = 81
    private var minecraftServerCache: MinecraftServer? = null

    private class FabricRegistryWrapper<T>(private val name: ResourceLocation, private val registry: Registry<T>) : RegistryWrapper<T> {
        override fun getId(something: T): Int {
            val id = registry.getId(something)
            if (id == -1) throw IllegalArgumentException()
            return id
        }

        override fun getKey(something: T): ResourceLocation {
            return registry.getKey(something) ?: throw IllegalArgumentException()
        }

        override fun get(location: ResourceLocation): T {
            return registry.get(location) ?: throw IllegalArgumentException()
        }

        override fun get(id: Int): T {
            return registry.byId(id) ?: throw IllegalArgumentException()
        }

        override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> {
            return registry.getTag(tagKey)
        }

        override fun get(resourceKey: ResourceKey<T>): Optional<Holder.Reference<T>> {
            return registry.getHolder(resourceKey)
        }

        override fun tryGet(location: ResourceLocation): T? {
            return registry.get(location)
        }

        override fun iterator(): Iterator<T> {
            return registry.iterator()
        }

        override fun keySet(): Set<ResourceLocation> {
            return registry.keySet()
        }
    }

    override val fluidCompactDivider: Int
        get() = FORGE_COMPACT_DEVIDER

    override var minecraftServer: MinecraftServer?
        get() = minecraftServerCache
        set(value) {
            minecraftServerCache = value
        }

    override fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        @Suppress("UNCHECKED_CAST")
        val targetRegistry: Registry<T> = (BuiltInRegistries.REGISTRY.get(registry.location()) ?: throw IllegalArgumentException("Cannot find registry $registry")) as Registry<T>
        return FabricRegistryWrapper(registry.location(), targetRegistry)
    }

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer {
        return FabricFakePlayer.create(level, profile)
    }

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity) {
            return entity.access
        }
        return null
    }

    override fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral? {
        return PeripheralLookup.get().find(level, pos, side)
    }

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        if (player.server.isUnderSpawnProtection(player.serverLevel(), pos, player)) {
            return true
        }
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(player.level(), player, pos, state, null)
    }

    override fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult {
        val fabricInteraction = UseEntityCallback.EVENT.invoker().interact(player, entity.level(), InteractionHand.MAIN_HAND, entity, hit)
        if (fabricInteraction.consumesAction()) {
            return fabricInteraction
        }
        val entityInteraction = entity.interactAt(player, hit.location.subtract(entity.position()), InteractionHand.MAIN_HAND)
        if (entityInteraction.consumesAction()) {
            return entityInteraction
        }
        return player.interactOn(entity, hand)
    }

    override fun useOn(
        player: ServerPlayer,
        stack: ItemStack,
        hit: BlockHitResult,
        canUseBlock: Predicate<BlockState>,
    ): InteractionResult {
        val result = UseBlockCallback.EVENT.invoker().interact(player, player.level(), InteractionHand.MAIN_HAND, hit)
        if (result != InteractionResult.PASS) return result
        val block = player.level().getBlockState(hit.blockPos)
        if (!block.isAir && canUseBlock.test(block)) {
            val useResult = block.use(player.level(), player, InteractionHand.MAIN_HAND, hit)
            if (useResult.consumesAction()) return useResult
        }
        return stack.useOn(UseOnContext(player, InteractionHand.MAIN_HAND, hit))
    }

    override fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean): Boolean {
        return level.setChunkForced(chunkPos.x, chunkPos.z, add)
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

    override fun <T : Entity> createEntityType(
        name: ResourceLocation,
        factory: Function<Level, T>,
    ): EntityType<T> {
        return FabricEntityTypeBuilder.create(MobCategory.MISC) { _, level -> factory.apply(level) }.build()
    }

    override fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block,
    ): BlockEntityType<T> {
        return FabricBlockEntityTypeBuilder.create({ t: BlockPos, u: BlockState ->
            factory.apply(t, u)
        }).addBlock(block).build()
    }

    override fun createTabBuilder(): CreativeModeTab.Builder {
        return FabricItemGroup.builder()
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
        }
    }

    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
        player.openMenu(WrappedMenuProvider(owner, savingFunction))
    }

    @JvmRecord
    private data class WrappedMenuProvider(val owner: MenuProvider, val savingFunction: SavingFunction) :
        ExtendedScreenHandlerFactory {
        override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
            return owner.createMenu(id, inventory, player)
        }

        override fun getDisplayName(): Component {
            return owner.displayName
        }

        override fun writeScreenOpeningData(player: ServerPlayer, buf: FriendlyByteBuf) {
            savingFunction.toBytes(buf)
        }
    }

    override fun registerGenericPeripheralLookup() {
        PeripheralLookup.get().registerFallback { _, _, _, blockEntity, context ->
            if (blockEntity is IPeripheralProvider<*>) {
                return@registerFallback blockEntity.getPeripheral(context)
            }
            return@registerFallback null
        }
    }
}
