package site.siredvin.peripheralium.forge

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.impl.PocketUpgrades
import dan200.computercraft.impl.TurtleUpgrades
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.util.NBTUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
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
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.level.BlockEvent.BreakEvent
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.registries.ForgeRegistry
import net.minecraftforge.registries.RegistryManager
import site.siredvin.peripheralium.ForgePeripheralium
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.RegistryWrapper
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier


class ForgePeripheraliumPlatform: PeripheraliumPlatform {
    private class ForgeRegistryWrapper<T>(private val name: ResourceLocation, private val registry: ForgeRegistry<T>): RegistryWrapper<T> {
        override fun getId(something: T): Int {
            val id = registry.getID(something)
            if (id == -1) throw IllegalArgumentException()
            return id;
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

    }

    override val fluidCompactDivider: Double
        get() = 1.0

    override fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        return ForgeRegistryWrapper(registry.location(), RegistryManager.ACTIVE.getRegistry(registry));
    }

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer {
        return ForgeFakePlayer(level, profile)
    }

    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        return ForgePeripheralium.itemsRegistry.register(key.path, item)
    }

    override fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        PeripheraliumCore.LOGGER.warn("Register block", )
        val blockRegister = ForgePeripheralium.blocksRegistry.register(key.path, block)
        ForgePeripheralium.itemsRegistry.register(key.path) { itemFactory(blockRegister.get()) }
        return blockRegister
    }

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity)
            return entity.access
        return null
    }

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        val event = BreakEvent(player.level, pos, state, player)
        MinecraftForge.EVENT_BUS.post(event)
        return event.isCanceled
    }

    override fun interactWithEntity(
        player: ServerPlayer,
        hand: InteractionHand,
        entity: Entity,
        hit: EntityHitResult
    ): InteractionResult {
        // Copied from CC:T to have nearly same logic here :)
        // Our behaviour is slightly different here - we call onInteractEntityAt before the interact methods, while
        // Forge does the call afterward (on the server, not on the client).
        var interactAt = ForgeHooks.onInteractEntityAt(player, entity, hit.location, hand);
        if (interactAt == null) {
            interactAt = entity.interactAt(player, hit.location.subtract(entity.position()), InteractionHand.MAIN_HAND);
        }

        if (interactAt.consumesAction())
            return interactAt

        return player.interactOn(entity, hand)
    }

    override fun useOn(
        player: ServerPlayer,
        stack: ItemStack,
        hit: BlockHitResult,
        canUseBlock: Predicate<BlockState>
    ): InteractionResult {
        val level = player.level
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
}