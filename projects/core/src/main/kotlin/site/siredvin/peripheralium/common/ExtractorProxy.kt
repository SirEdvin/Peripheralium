package site.siredvin.peripheralium.common

import net.minecraft.world.Container
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import site.siredvin.peripheralium.api.ContainerExtractor

object ExtractorProxy {

    private val ADDITIONAL_CONTAINER_EXTRACTORS: MutableList<ContainerExtractor> = mutableListOf()

    init {
        addContainerExtractor(MinecartHelpers::minecartExtractor)
    }

    fun addContainerExtractor(extractor: ContainerExtractor) {
        ADDITIONAL_CONTAINER_EXTRACTORS.add(extractor)
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

    fun extractCCItemStorage(level: Level, obj: Any?): Container? {
        for (extractor in ADDITIONAL_CONTAINER_EXTRACTORS) {
            val result = extractor.extract(level, obj)
            if (result != null)
                return result
        }

        if (obj is BlockEntity) {
            if (obj.isRemoved)
                return null
            val container = extractContainerFromBlockEntity(obj)
            if (container != null)
                return container
        }
        if (obj is Container)
            return obj
        return null
    }
}