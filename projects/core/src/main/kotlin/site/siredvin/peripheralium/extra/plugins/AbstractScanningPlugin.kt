package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import site.siredvin.peripheralium.api.datatypes.AreaInteractionMode
import site.siredvin.peripheralium.api.peripheral.IPeripheralOperation
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.api.peripheral.IPeripheralPlugin
import site.siredvin.peripheralium.computercraft.operations.SphereOperationContext
import site.siredvin.peripheralium.util.assertBetween
import site.siredvin.peripheralium.util.representation.LuaRepresentation
import site.siredvin.peripheralium.util.world.ScanUtils
import java.util.function.BiConsumer
import java.util.function.Predicate
import java.util.stream.Collectors
import kotlin.math.min

abstract class AbstractScanningPlugin(protected val owner: IPeripheralOwner) : IPeripheralPlugin {

    abstract val scanRadius: Int
    abstract val allowedMods: Set<AreaInteractionMode>

    abstract val blockStateEnriches: List<BiConsumer<BlockState, MutableMap<String, Any>>>
    abstract val itemEnriches: List<BiConsumer<ItemEntity, MutableMap<String, Any>>>
    abstract val entityEnriches: List<BiConsumer<Entity, MutableMap<String, Any>>>

    abstract val scanEntitiesOperation: IPeripheralOperation<SphereOperationContext>?
    abstract val scanItemsOperation: IPeripheralOperation<SphereOperationContext>?
    abstract val scanBlocksOperation: IPeripheralOperation<SphereOperationContext>?

    abstract val suitableEntity: Predicate<Entity>

    private fun entityConverter(entity: Entity, facing: Direction, center: BlockPos): MutableMap<String, Any> {
        val base = LuaRepresentation.withPos(entity, facing, center, LuaRepresentation::forEntity)
        entityEnriches.forEach { it.accept(entity, base) }
        return base
    }

    private fun blockStateConverter(state: BlockState, pos: BlockPos, facing: Direction, center: BlockPos): MutableMap<String, Any> {
        val base = LuaRepresentation.withPos(state, pos, facing, center, LuaRepresentation::forBlockState)
        blockStateEnriches.forEach { it.accept(state, base) }
        return base
    }

    private fun itemConverter(entity: ItemEntity, facing: Direction, center: BlockPos): MutableMap<String, Any> {
        val base = LuaRepresentation.withPos(entity, facing, center) { LuaRepresentation.forItemStack(it.item) }
        itemEnriches.forEach { it.accept(entity, base) }
        return base
    }

    private fun getBox(pos: BlockPos, radius: Int): AABB {
        val x: Int = pos.x
        val y: Int = pos.y
        val z: Int = pos.z
        val interactionRadius = min(radius, scanRadius)
        return AABB(
            (x - interactionRadius).toDouble(),
            (y - interactionRadius).toDouble(),
            (z - interactionRadius).toDouble(),
            (x + interactionRadius).toDouble(),
            (y + interactionRadius).toDouble(),
            (z + interactionRadius).toDouble(),
        ).inflate(0.99)
    }

    private fun scanBlocks(radius: Int): List<Pair<BlockState, BlockPos>> {
        val result = mutableListOf<Pair<BlockState, BlockPos>>()
        ScanUtils.traverseBlocks(
            owner.level!!,
            owner.pos,
            min(radius, scanRadius),
            { state, pos -> result.add(Pair(state, pos)) },
            relativePosition = false,
        )
        return result
    }

    private fun <T : Entity> scanEntities(entityClass: Class<T>, radius: Int): List<T> {
        return owner.level!!.getEntitiesOfClass(entityClass, getBox(owner.pos, radius))
    }

    private fun scanItems(radius: Int): List<ItemEntity> {
        return scanEntities(ItemEntity::class.java, radius)
    }

    private fun scanLivingEntities(radius: Int): List<LivingEntity> {
        return scanEntities(LivingEntity::class.java, radius)
    }

    @LuaFunction(mainThread = true)
    fun scan(arguments: IArguments): MethodResult {
        val mode = AreaInteractionMode.luaValueOf(arguments.getString(0), allowedMods)
        val radius = arguments.optInt(1, scanRadius)
        assertBetween(radius, 1, scanRadius, "radius")
        return when (mode) {
            AreaInteractionMode.ITEM -> owner.withOperation(scanItemsOperation!!, SphereOperationContext.of(radius), {
                MethodResult.of(
                    scanItems(radius).stream().map { itemConverter(it, owner.facing, owner.pos) }
                        .collect(Collectors.toList()),
                )
            })
            AreaInteractionMode.ENTITY -> owner.withOperation(scanBlocksOperation!!, SphereOperationContext.of(radius), {
                MethodResult.of(
                    scanLivingEntities(radius).stream()
                        .filter { suitableEntity.test(it) }.map { entityConverter(it, owner.facing, owner.pos) }.collect(Collectors.toList()),
                )
            })
            AreaInteractionMode.BLOCK -> owner.withOperation(scanEntitiesOperation!!, SphereOperationContext.of(radius), {
                MethodResult.of(
                    scanBlocks(radius).stream().map { blockStateConverter(it.first, it.second, owner.facing, owner.pos) }.collect(Collectors.toList()),
                )
            })
        }
    }
}
