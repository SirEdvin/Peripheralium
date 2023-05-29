package site.siredvin.peripheralium.util.world

import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerPlayerGameMode
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.api.storage.ContainerUtils
import site.siredvin.peripheralium.ext.toBlockPos
import site.siredvin.peripheralium.util.Pair
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*
import java.util.function.Predicate

class FakePlayerProxy(val fakePlayer: ServerPlayer, private val range: Int = 4) {

    companion object {
        val DUMMY_PROFILE = GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b276"), "[" + PeripheraliumCore.MOD_ID + "]")
        private val collidablePredicate = EntitySelector.NO_SPECTATORS
    }

    private var digPosition: BlockPos? = null
    private var digBlock: Block? = null
    private var currentDamage = 0f

    private val level: Level
        get() = fakePlayer.level

    private val gameMode: ServerPlayerGameMode
        get() = fakePlayer.gameMode

    private val inventory: Container
        get() = fakePlayer.inventory

    private fun setState(block: Block?, pos: BlockPos?) {
        if (digPosition != null) {
            gameMode.handleBlockBreakAction(
                digPosition!!,
                ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
                Direction.EAST,
                1,
                1,
            )
        }
        digPosition = pos
        digBlock = block
        currentDamage = 0f
    }

    fun <T> withConsumer(entity: Entity, func: () -> (T)): T {
        DropConsumer.configure(entity, { stack: ItemStack -> ContainerUtils.storeItem(inventory, stack) })
        val result = func()
        DropConsumer.resetAndDrop(level, fakePlayer.blockPosition().above())
        return result
    }

    fun <T> withConsumer(level: Level, pos: BlockPos, func: () -> (T)): T {
        DropConsumer.configure(level, pos, { stack -> ContainerUtils.storeItem(inventory, stack) })
        val result = func()
        DropConsumer.resetAndDrop(level, fakePlayer.blockPosition().above())
        return result
    }

    fun findHit(skipEntity: Boolean, skipBlock: Boolean): HitResult {
        return findHit(skipEntity, skipBlock, null)
    }

    fun findHit(skipEntity: Boolean, skipBlock: Boolean, entityFilter: Predicate<Entity>?): HitResult {
        val origin = Vec3(fakePlayer.x, fakePlayer.y, fakePlayer.z)
        val look = fakePlayer.lookAngle
        val target = Vec3(origin.x + look.x * range, origin.y + look.y * range, origin.z + look.z * range)
        val traceContext = ClipContext(origin, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fakePlayer)
        val directionVec = traceContext.from.subtract(traceContext.to)
        val traceDirection = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z)
        val blockHit: HitResult = if (skipBlock) {
            BlockHitResult.miss(traceContext.to, traceDirection, traceContext.to.toBlockPos())
        } else {
            BlockGetter.traverseBlocks(
                traceContext.from,
                traceContext.to,
                traceContext,
                { _: ClipContext?, blockPos: BlockPos ->
                    if (level.isEmptyBlock(blockPos)) {
                        return@traverseBlocks null
                    }
                    BlockHitResult(
                        Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()),
                        traceDirection,
                        blockPos,
                        false,
                    )
                },
            ) { rayTraceContext: ClipContext ->
                BlockHitResult.miss(
                    rayTraceContext.to,
                    traceDirection,
                    rayTraceContext.to.toBlockPos(),
                )
            }!!
        }
        if (skipEntity) {
            return blockHit
        }
        val entities = level.getEntities(
            fakePlayer,
            fakePlayer.boundingBox.expandTowards(look.x * range, look.y * range, look.z * range).inflate(1.0, 1.0, 1.0),
            collidablePredicate,
        )
        var closestEntity: LivingEntity? = null
        var closestVec: Vec3? = null
        var closestDistance = 0.0
        for (entityHit in entities) {
            if (entityHit !is LivingEntity) continue
            if (entityFilter != null && !entityFilter.test(entityHit)) continue
            // Add litter bigger that just pick radius
            val box = entityHit.getBoundingBox().inflate(entityHit.getPickRadius() + 0.5)
            val clipResult = box.clip(origin, target)
            if (box.contains(origin)) {
                if (closestDistance >= 0.0) {
                    closestEntity = entityHit
                    closestVec = clipResult.orElse(origin)
                    closestDistance = 0.0
                }
            } else if (clipResult.isPresent) {
                val clipVec = clipResult.get()
                val distance = origin.distanceTo(clipVec)
                if (distance < closestDistance || closestDistance == 0.0) {
                    if (entityHit === entityHit.getRootVehicle()) {
                        if (closestDistance == 0.0) {
                            closestEntity = entityHit
                            closestVec = clipVec
                        }
                    } else {
                        closestEntity = entityHit
                        closestVec = clipVec
                        closestDistance = distance
                    }
                }
            }
        }
        return if (closestEntity != null && closestDistance <= range && (
                blockHit.type == HitResult.Type.MISS || fakePlayer.distanceToSqr(
                    blockHit.location,
                ) > closestDistance * closestDistance
                )
        ) {
            EntityHitResult(closestEntity, closestVec!!)
        } else {
            blockHit
        }
    }

    fun useOnSpecificEntity(entity: Entity, result: HitResult): InteractionResult {
        return PeripheraliumPlatform.interactWithEntity(fakePlayer, InteractionHand.MAIN_HAND, entity, result as EntityHitResult)
    }

    fun use(skipEntity: Boolean, skipBlock: Boolean, entityFilter: Predicate<Entity>?): InteractionResult {
        val hit = findHit(skipEntity, skipBlock, entityFilter)
        if (hit is BlockHitResult) {
            return withConsumer(level, hit.blockPos) {
                val useOnResult = PeripheraliumPlatform.useOn(fakePlayer, fakePlayer.mainHandItem, hit) { true }
                if (useOnResult.consumesAction()) {
                    return@withConsumer useOnResult
                }
                level.destroyBlockProgress(fakePlayer.id, hit.blockPos, -1)
                val useItemResult = gameMode.useItemOn(fakePlayer, level, fakePlayer.mainHandItem, InteractionHand.MAIN_HAND, hit)
                if (useItemResult.consumesAction()) {
                    return@withConsumer useItemResult
                }
                return@withConsumer gameMode.useItem(fakePlayer, level, fakePlayer.mainHandItem, InteractionHand.MAIN_HAND)
            }
        }
        if (hit is EntityHitResult) {
            return withConsumer(hit.entity) { useOnSpecificEntity(hit.entity, hit) }
        }
        return InteractionResult.FAIL
    }

    fun use(skipEntity: Boolean, skipBlock: Boolean): InteractionResult {
        return use(skipEntity, skipBlock, null)
    }

    fun swing(skipEntity: Boolean, skipBlock: Boolean, entityFilter: Predicate<Entity>?): Pair<Boolean, String> {
        val hit = findHit(skipEntity = skipEntity, skipBlock = skipBlock, entityFilter = entityFilter)
        if (hit.type == HitResult.Type.MISS) {
            return Pair.of(false, "Nothing to swing")
        }
        if (hit is BlockHitResult) {
            return swingBlock(hit)
        }
        if (hit is EntityHitResult) {
            return swingEntity(hit)
        }
        return Pair.of(false, "Nothing found")
    }

    fun swingBlock(hit: BlockHitResult): Pair<Boolean, String> {
        val pos = BlockPos(hit.location.x.toInt(), hit.location.y.toInt(), hit.location.z.toInt())
        val state = level.getBlockState(pos)
        val block = state.block
        val tool = fakePlayer.mainHandItem
        if (block != digBlock || pos != digPosition) {
            setState(block, pos)
        }
        if (!level.isEmptyBlock(pos) && !state.material.isLiquid) {
            if (PeripheraliumPlatform.isBlockProtected(pos, state, fakePlayer)) {
                return Pair.of(false, "Cannot break protected block")
            }
            if (block == Blocks.BEDROCK || state.getDestroySpeed(level, pos) <= -1f) {
                return Pair.of(false, "Unbreakable block detected")
            }
            val breakSpeed = 0.5f * tool.getDestroySpeed(state) / state.getDestroySpeed(level, pos) - 0.1f
            currentDamage += 9 * breakSpeed
            level.destroyBlockProgress(fakePlayer.id, pos, currentDamage.toInt())
            if (currentDamage > 9) {
                withConsumer(level, pos) {
                    level.playSound(null, pos, state.soundType.breakSound, SoundSource.NEUTRAL, .25f, 1f)
                    gameMode.handleBlockBreakAction(pos, ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, fakePlayer.direction.opposite, 1, 1)
                    gameMode.destroyBlock(pos)
                    level.destroyBlockProgress(fakePlayer.id, pos, -1)
                    setState(null, null)
                }
            }
            return Pair.of(true, "")
        }
        return Pair.of(false, "Nothing to dig here")
    }

    fun swingEntity(hit: EntityHitResult): Pair<Boolean, String> {
        val tool = fakePlayer.mainHandItem
        if (tool.isEmpty) {
            return Pair.of(false, "Cannot swing without tool")
        }
        val entity = hit.entity
        if (entity !is LivingEntity) {
            return Pair.of(false, "Incorrect entity hit")
        }
        if (!fakePlayer.canAttack(entity)) {
            return Pair.of(false, "Can't swing this entity")
        }
        withConsumer(entity) { fakePlayer.attack(entity) }
        fakePlayer.cooldowns.addCooldown(tool.item, 1)
        return Pair.of(true, "")
    }
}
