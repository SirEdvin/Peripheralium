package site.siredvin.peripheralium.storages.item

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import site.siredvin.peripheralium.storages.ContainerWrapper
import site.siredvin.peripheralium.storages.LimitedInventory

object ItemStorageExtractor {

    fun interface TargetableStorageExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): SlottedItemSink?
    }

    fun interface StorageExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): ItemStorage?
    }

    fun interface TargetableStorageEntityExtractor {
        fun extract(level: Level, entity: Entity): SlottedItemSink?
    }

    fun interface StorageEntityExtractor {
        fun extract(level: Level, entity: Entity): ItemStorage?
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

    fun extractStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): ItemStorage? {
        for (extractor in ADDITIONAL_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }

        if (blockEntity != null) {
            if (blockEntity.isRemoved) {
                return null
            }
            val container = extractContainerFromBlockEntity(blockEntity)
            if (container != null) {
                return ContainerWrapper(container)
            }
        }
        if (blockEntity is Container) {
            return ContainerWrapper(blockEntity)
        }
        return null
    }

    fun extractStorage(level: Level, entity: Entity): ItemStorage? {
        for (extractor in ADDITIONAL_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        if (entity is Player) {
            return ContainerWrapper(entity.inventory)
        }
        if (entity is AbstractChestedHorse && entity.hasChest()) {
            return ContainerWrapper(LimitedInventory(entity.inventory, IntArray(entity.inventory.containerSize - 2) { i -> i + 2 }))
        }
        return null
    }

    fun extractStorageFromUnknown(level: Level, obj: Any?): ItemStorage? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractStorage(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractStorage(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractStorage(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractItemSink(level: Level, pos: BlockPos, blockEntity: BlockEntity?): ItemSink? {
        val storage = extractStorage(level, pos, blockEntity)
        if (storage != null) {
            return storage
        }

        for (extractor in ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractItemSink(level: Level, entity: Entity): ItemSink? {
        val storage = extractStorage(level, entity)
        if (storage != null) {
            return storage
        }
        for (extractor in ADDITIONAL_TARGETABLE_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractItemSinkFromUnknown(level: Level, obj: Any?): ItemSink? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractItemSink(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractItemSink(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractItemSink(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
