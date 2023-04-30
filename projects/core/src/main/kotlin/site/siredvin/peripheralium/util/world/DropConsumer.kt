package site.siredvin.peripheralium.util.world

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import site.siredvin.peripheralium.util.ContainerHelpers

object DropConsumer {
    private var consumer: ((ItemStack) -> ItemStack)? = null
    private var remainingDrop: MutableList<ItemStack>? = null
    private var targetLevel: Level? = null
    private var targetEntity: Entity? = null
    private var dropBounds: AABB? = null

    fun configure(level: Level, pos: BlockPos, consumer: ((ItemStack) -> ItemStack), range: Double = 2.0) {
        remainingDrop = mutableListOf()
        targetLevel = level
        this.consumer = consumer
        targetEntity = null
        dropBounds = AABB(pos).inflate(range)
    }

    fun configure(entity: Entity, consumer: ((ItemStack) -> ItemStack), range: Double = 2.0) {
        remainingDrop = mutableListOf()
        targetLevel = entity.level
        this.consumer = consumer
        targetEntity = null
        dropBounds = AABB(entity.blockPosition()).inflate(range)
    }

    fun reset(): List<ItemStack> {
        val result = remainingDrop ?: throw IllegalCallerException("Double reset detected")

        remainingDrop = null
        consumer = null
        targetEntity = null
        targetLevel = null
        dropBounds = null

        return result
    }

    fun resetAndDrop(level: Level, pos: BlockPos) {
        val toDrop = reset()
        toDrop.forEach {
            Containers.dropItemStack(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), it)
        }
    }

    fun consume(stack: ItemStack) {
        val leftStack = consumer!!(stack)
        if (!leftStack.isEmpty)
            remainingDrop!!.add(leftStack)
    }

    fun onEntitySpawn(entity: Entity): Boolean {
        if (targetLevel == entity.level && entity is ItemEntity && dropBounds!!.contains(entity.position())) {
            consume(entity.item)
            return true
        }
        return false
    }

    fun onLivingDrop(entity: Entity, stack: ItemStack): Boolean {
        if (entity != targetEntity)
            return false
        consume(stack)
        return true
    }

}