package site.siredvin.peripheralium.api.storage

import net.minecraft.world.Container
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity

object ExtractorProxy {

    fun interface TargetableStorageExtractor {
        fun extract(level: Level, obj: Any?): TargetableStorage?
    }

    fun interface StorageExtractor {
        fun extract(level: Level, obj: Any?): Storage?
    }

    private val ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS: MutableList<TargetableStorageExtractor> = mutableListOf()
    private val ADDITIONAL_STORAGE_EXTRACTORS: MutableList<StorageExtractor> = mutableListOf()

    fun addTargetableStorageExtractor(extractor: TargetableStorageExtractor) {
        ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addStorageExtractor(extractor: StorageExtractor) {
        ADDITIONAL_STORAGE_EXTRACTORS.add(extractor)
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

    fun extractStorage(level: Level, obj: Any?): Storage? {
        for (extractor in ADDITIONAL_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, obj)
            if (result != null)
                return result
        }

        if (obj is BlockEntity) {
            if (obj.isRemoved)
                return null
            val container = extractContainerFromBlockEntity(obj)
            if (container != null)
                return TargetableContainer(container)
        }
        if (obj is Container)
            return TargetableContainer(obj)
        return null
    }

    fun extractTargetableStorage(level: Level, obj: Any?): TargetableStorage? {
        val storage = extractStorage(level, obj)
        if (storage != null)
            return storage

        for (extractor in ADDITIONAL_TARGETABLE_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, obj)
            if (result != null)
                return result
        }
        return null
    }
}