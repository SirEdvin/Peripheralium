package site.siredvin.peripheralium.fabric

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.impl.PocketUpgrades
import dan200.computercraft.impl.TurtleUpgrades
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.util.NBTUtil
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
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
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RegistryWrapper
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier


class FabricPeripheraliumPlatform: PeripheraliumPlatform {

    companion object {
        const val FORGE_COMPACT_DEVIDER = 81.0
    }
    
    private class FabricRegistryWrapper<T>(private val name: ResourceLocation, private val registry: Registry<T>): RegistryWrapper<T> {
        override fun getId(something: T): Int {
            val id = registry.getId(something)
            if (id == -1) throw IllegalArgumentException()
            return id;
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

    }

    override val fluidCompactDivider: Double
        get() = FORGE_COMPACT_DEVIDER

    override fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        val targetRegistry: Registry<T> = (BuiltInRegistries.REGISTRY.get(registry.location()) ?: throw IllegalArgumentException("Cannot find registry $registry")) as Registry<T>
        return FabricRegistryWrapper(registry.location(), targetRegistry)
    }

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer {
        return FabricFakePlayer.create(level, profile)
    }

    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = Registry.register(BuiltInRegistries.ITEM, key, item.get())
        return Supplier { registeredItem }
    }

    override fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, key, block.get())
        Registry.register(BuiltInRegistries.ITEM, key, itemFactory(registeredBlock))
        return Supplier { registeredBlock }
    }

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity)
            return entity.access
        return null
    }

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        if (player.level.server?.isUnderSpawnProtection(player.level as ServerLevel, pos, player) == true)
            return true
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(player.level, player, pos, state, null)
    }

    override fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult {
        val fabricInteraction = UseEntityCallback.EVENT.invoker().interact(player, entity.level, InteractionHand.MAIN_HAND, entity, hit)
        if (fabricInteraction.consumesAction())
            return fabricInteraction
        val entityInteraction = entity.interactAt(player, hit.location.subtract(entity.position()), InteractionHand.MAIN_HAND)
        if (entityInteraction.consumesAction())
            return entityInteraction
        return player.interactOn(entity, hand)
    }

    override fun useOn(
        player: ServerPlayer,
        stack: ItemStack,
        hit: BlockHitResult,
        canUseBlock: Predicate<BlockState>
    ): InteractionResult {
        val result = UseBlockCallback.EVENT.invoker().interact(player, player.level, InteractionHand.MAIN_HAND, hit)
        if (result != InteractionResult.PASS) return result
        val block = player.level.getBlockState(hit.blockPos)
        if (!block.isAir && canUseBlock.test(block)) {
            val useResult = block.use(player.level, player, InteractionHand.MAIN_HAND, hit)
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

    override fun getTurtleUpgrade(stack: ItemStack): ITurtleUpgrade? {
        return TurtleUpgrades.instance().get(stack)
    }

    override fun getPocketUpgrade(stack: ItemStack): IPocketUpgrade? {
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
        factory: Function<Level, T>
    ): EntityType<T> {
        return FabricEntityTypeBuilder.create(MobCategory.MISC) { _, level -> factory.apply(level) }.build()
    }

    override fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block
    ): BlockEntityType<T> {
        return FabricBlockEntityTypeBuilder.create({ t: BlockPos, u: BlockState ->
            factory.apply(t, u)
        }).addBlock(block).build()
    }

    override fun createTurtlesWithUpgrade(upgrade: ITurtleUpgrade): List<ItemStack> {
        return listOf(
            ModRegistry.Items.TURTLE_NORMAL.get().create(-1, null, -1, null, upgrade, 0, null),
            ModRegistry.Items.TURTLE_ADVANCED.get().create(-1, null, -1, null, upgrade, 0, null),
        )
    }

    override fun createPocketsWithUpgrade(upgrade: IPocketUpgrade): List<ItemStack> {
        return listOf(
            ModRegistry.Items.POCKET_COMPUTER_NORMAL.get().create(-1, null, -1, upgrade),
            ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get().create(-1, null, -1, upgrade),
        )
    }

    override fun isOre(block: BlockState): Boolean {
        return block.`is`(ConventionalBlockTags.ORES)
    }
}