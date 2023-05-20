package site.siredvin.peripheralium.api.storage

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse
import net.minecraft.world.entity.animal.horse.AbstractHorse
import net.minecraft.world.entity.animal.horse.Horse
import net.minecraft.world.entity.animal.horse.Llama
import net.minecraft.world.entity.animal.horse.Mule
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import site.siredvin.peripheralium.util.LimitedInventory

object ExtractorProxy {

    fun interface TargetableStorageExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): TargetableStorage?
    }

    fun interface StorageExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): Storage?
    }

    fun interface TargetableStorageEntityExtractor {
        fun extract(level: Level, entity: Entity): TargetableStorage?
    }

    fun interface StorageEntityExtractor {
        fun extract(level: Level, entity: Entity): Storage?
    }

    private val ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS: MutableList<TargetableStorageExtractor> = mutableListOf()
    private val ADDITIONAL_STORAGE_EXTRACTORS: MutableList<StorageExtractor> = mutableListOf()

    private val ADDITIONAL_TARGETABLE_STORAGE_ENTITY_EXTRACTORS: MutableList<TargetableStorageEntityExtractor> = mutableListOf()
    private val ADDITIONAL_STORAGE_ENTITY_EXTRACTORS: MutableList<StorageEntityExtractor> = mutableListOf()

    fun addTargetableStorageExtractor(extractor: TargetableStorageExtractor) {
        ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addStorageExtractor(extractor: StorageExtractor) {
        ADDITIONAL_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addTargetableStorageExtractor(extractor: TargetableStorageEntityExtractor) {
        ADDITIONAL_TARGETABLE_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addStorageExtractor(extractor: StorageEntityExtractor) {
        ADDITIONAL_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    /**
     * Copied from old CC:T InventoryUtil because of nasty double chest hack logic
     */
    fun extractContainerFromBlockEntity(blockEntity: BlockEntity): Container? {
        val level = blockEntity.level
        val pos = blockEntity.blockPos
        val blockState = level!!.getBlockState(pos)
        val block = blockState.block
        return if (blockEntity is Container) {
            if (blockEntity is ChestBlockEntity && block is ChestBlock) {
                ChestBlock.getContainer(block, blockState, level, pos, true)
            } else {
                blockEntity
            }
        } else {
            null
        }
    }

    fun extractStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): Storage? {
        for (extractor in ADDITIONAL_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null)
                return result
        }

        if (blockEntity != null) {
            if (blockEntity.isRemoved)
                return null
            val container = extractContainerFromBlockEntity(blockEntity)
            if (container != null)
                return TargetableContainer(container)
        }
        if (blockEntity is Container)
            return TargetableContainer(blockEntity)
        return null
    }

    fun extractStorage(level: Level, entity: Entity): Storage? {
        for (extractor in ADDITIONAL_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null)
                return result
        }
        if (entity is Player)
            return TargetableContainer(entity.inventory)
        if (entity is AbstractChestedHorse && entity.hasChest())
            return TargetableContainer(LimitedInventory(entity.inventory, IntArray(entity.inventory.containerSize - 2) { i -> i + 2 } ))
        return null
    }

    fun extractStorageFromUnknown(level: Level, obj: Any?): Storage? {
        if (obj == null)
            return null
        if (obj is BlockPos)
            return extractStorage(level, obj, level.getBlockEntity(obj))
        if (obj is BlockEntity)
            return extractStorage(level, obj.blockPos, obj)
        if (obj is Entity)
            return extractStorage(level, obj)
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractTargetableStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): TargetableStorage? {
        val storage = extractStorage(level, pos, blockEntity)
        if (storage != null)
            return storage

        for (extractor in ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null)
                return result
        }
        return null
    }

    fun extractTargetableStorage(level: Level, entity: Entity): TargetableStorage? {
        val storage = extractStorage(level, entity)
        if (storage != null)
            return storage
        for (extractor in ADDITIONAL_TARGETABLE_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null)
                return result
        }
        return null
    }

    fun extractTargetableStorageFromUnknown(level: Level, obj: Any?): TargetableStorage? {
        if (obj == null)
            return null
        if (obj is BlockPos)
            return extractTargetableStorage(level, obj, level.getBlockEntity(obj))
        if (obj is BlockEntity)
            return extractTargetableStorage(level, obj.blockPos, obj)
        if (obj is Entity)
            return extractTargetableStorage(level, obj)
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}