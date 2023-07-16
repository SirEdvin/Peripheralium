package site.siredvin.peripheralium.computercraft.peripheral.ability

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import site.siredvin.peripheralium.api.peripheral.IOwnerAbility
import site.siredvin.peripheralium.api.peripheral.IPeripheralOperation
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.computercraft.operations.SphereOperationContext
import site.siredvin.peripheralium.util.assertBetween
import site.siredvin.peripheralium.util.representation.LuaRepresentation
import site.siredvin.peripheralium.util.world.ScanUtils
import java.util.function.BiConsumer
import java.util.function.Predicate
import kotlin.math.min

class ScanningAbility<T : IPeripheralOwner>(val owner: T, val maxRadius: Int) : IOwnerAbility, IPeripheralPlugin {
    abstract class ScanningMethod<T : IPeripheralOwner>(val name: String, val operation: IPeripheralOperation<SphereOperationContext>) {
        abstract fun scan(ability: ScanningAbility<T>, radius: Int): MethodResult
    }

    class BlockScanningMethod<T : IPeripheralOwner>(operation: IPeripheralOperation<SphereOperationContext>, private val enriches: Array<out BiConsumer<BlockState, MutableMap<String, Any>>>) : ScanningMethod<T>("block", operation) {
        private fun blockStateConverter(state: BlockState, pos: BlockPos, facing: Direction, center: BlockPos): MutableMap<String, Any> {
            val base = LuaRepresentation.withPos(state, pos, facing, center, LuaRepresentation::forBlockState)
            enriches.forEach { it.accept(state, base) }
            return base
        }
        override fun scan(ability: ScanningAbility<T>, radius: Int): MethodResult {
            val result = mutableListOf<MutableMap<String, Any>>()
            ScanUtils.traverseBlocks(
                ability.owner.level!!,
                ability.owner.pos,
                min(radius, ability.maxRadius),
                { state, pos -> blockStateConverter(state, pos, ability.owner.facing, ability.owner.pos) },
                relativePosition = false,
            )
            return MethodResult.of(result)
        }
    }

    abstract class EntityScanningMethod<T : IPeripheralOwner, V : Entity>(
        name: String,
        operation: IPeripheralOperation<SphereOperationContext>,
        private val entityClass: Class<V>,
        private val predicate: Predicate<V> = Predicate { true },
    ) : ScanningMethod<T>(name, operation) {
        private fun getBox(ability: ScanningAbility<T>, pos: BlockPos, radius: Int): AABB {
            val x: Int = pos.x
            val y: Int = pos.y
            val z: Int = pos.z
            val interactionRadius = min(radius, ability.maxRadius)
            return AABB(
                (x - interactionRadius).toDouble(),
                (y - interactionRadius).toDouble(),
                (z - interactionRadius).toDouble(),
                (x + interactionRadius).toDouble(),
                (y + interactionRadius).toDouble(),
                (z + interactionRadius).toDouble(),
            ).inflate(0.99)
        }

        abstract fun convert(entity: V, ability: ScanningAbility<T>): Map<String, Any>

        override fun scan(ability: ScanningAbility<T>, radius: Int): MethodResult {
            return MethodResult.of(
                ability.owner.level!!.getEntitiesOfClass(entityClass, getBox(ability, ability.owner.pos, radius)).filter(
                    predicate::test,
                ).map {
                    convert(it, ability)
                },
            )
        }
    }

    class ItemEntityScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<ItemStack, MutableMap<String, Any>>>,
    ) : EntityScanningMethod<T, ItemEntity>("item", operation, ItemEntity::class.java) {
        override fun convert(entity: ItemEntity, ability: ScanningAbility<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos) { LuaRepresentation.forItemStack(it.item) }
            enriches.forEach { it.accept(entity.item, base) }
            return base
        }
    }
    class XpEntityScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<ExperienceOrb, MutableMap<String, Any>>>,
    ) : EntityScanningMethod<T, ExperienceOrb>("xp", operation, ExperienceOrb::class.java) {
        override fun convert(entity: ExperienceOrb, ability: ScanningAbility<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos) { LuaRepresentation.forExpirenceOrb(it) }
            enriches.forEach { it.accept(entity, base) }
            return base
        }
    }
    class LivingEntityScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<LivingEntity, MutableMap<String, Any>>>,
        predicate: Predicate<LivingEntity>,
    ) : EntityScanningMethod<T, LivingEntity>("entity", operation, LivingEntity::class.java, predicate.and { it !is Player }) {
        override fun convert(entity: LivingEntity, ability: ScanningAbility<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos, LuaRepresentation::forLivingEntity)
            enriches.forEach { it.accept(entity, base) }
            return base
        }
    }

    class PlayerScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<Player, MutableMap<String, Any>>>,
    ) : EntityScanningMethod<T, Player>("player", operation, Player::class.java) {
        override fun convert(entity: Player, ability: ScanningAbility<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos) { LuaRepresentation.forPlayer(it) }
            enriches.forEach { it.accept(entity, base) }
            return base
        }
    }

    private val scanningMethods: MutableMap<String, ScanningMethod<T>> = mutableMapOf()

    override val operations: List<IPeripheralOperation<*>>
        get() = scanningMethods.map { it.value.operation }.toSet().toList()

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["maxRadius"] = maxRadius
    }

    fun attachBlockScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<BlockState, MutableMap<String, Any>>): ScanningAbility<T> {
        return attachScanningMethod(BlockScanningMethod(operation, enriches))
    }

    fun attachItemScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<ItemStack, MutableMap<String, Any>>): ScanningAbility<T> {
        return attachScanningMethod(ItemEntityScanningMethod(operation, enriches))
    }

    fun attachLivingEntityScan(operation: IPeripheralOperation<SphereOperationContext>, predicate: Predicate<LivingEntity>, vararg enriches: BiConsumer<LivingEntity, MutableMap<String, Any>>): ScanningAbility<T> {
        return attachScanningMethod(LivingEntityScanningMethod(operation, enriches, predicate))
    }

    fun attachXpScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<ExperienceOrb, MutableMap<String, Any>>): ScanningAbility<T> {
        return attachScanningMethod(XpEntityScanningMethod(operation, enriches))
    }

    fun attachPlayerScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<Player, MutableMap<String, Any>>): ScanningAbility<T> {
        return attachScanningMethod(PlayerScanningMethod(operation, enriches))
    }

    fun attachScanningMethod(method: ScanningMethod<T>): ScanningAbility<T> {
        this.scanningMethods[method.name] = method
        return this
    }

    @LuaFunction(mainThread = true)
    fun scan(arguments: IArguments): MethodResult {
        val mode = arguments.getString(0)
        val radius = arguments.optInt(1, maxRadius)
        assertBetween(radius, 1, maxRadius, "radius")
        val scanningMethod = scanningMethods[mode] ?: throw LuaException("There is no scanning method $mode")
        return owner.withOperation(scanningMethod.operation, SphereOperationContext.of(radius), {
            scanningMethod.scan(this, radius)
        })
    }
}
