package site.siredvin.peripheralium.storages.fluid

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

object FluidStorageExtractor {
    fun interface FluidSinkExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): FluidSink?
    }

    fun interface FluidStorageExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): FluidStorage?
    }

    fun interface FluidSinkEntityExtractor {
        fun extract(level: Level, entity: Entity): FluidSink?
    }

    fun interface FluidStorageEntityExtractor {
        fun extract(level: Level, entity: Entity): FluidStorage?
    }

    private val FLUID_SINK_EXTRACTORS: MutableList<FluidSinkExtractor> = mutableListOf()
    private val FLUID_STORAGE_EXTRACTORS: MutableList<FluidStorageExtractor> = mutableListOf()

    private val FLUID_SINK_ENTITY_EXTRACTORS: MutableList<FluidSinkEntityExtractor> = mutableListOf()
    private val FLUID_STORAGE_ENTITY_EXTRACTORS: MutableList<FluidStorageEntityExtractor> = mutableListOf()

    fun addFluidSinkExtractor(extractor: FluidSinkExtractor) {
        FLUID_SINK_EXTRACTORS.add(extractor)
    }

    fun addFluidStorageExtractor(extractor: FluidStorageExtractor) {
        FLUID_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addFluidSinkExtractor(extractor: FluidSinkEntityExtractor) {
        FLUID_SINK_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addFluidStorageExtractor(extractor: FluidStorageEntityExtractor) {
        FLUID_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    fun extractFluidStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): FluidStorage? {
        for (extractor in FLUID_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidStorage(level: Level, entity: Entity): FluidStorage? {
        for (extractor in FLUID_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidStorageFromUnknown(level: Level, obj: Any?): FluidStorage? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractFluidStorage(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractFluidStorage(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractFluidStorage(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractFluidSink(level: Level, pos: BlockPos, blockEntity: BlockEntity?): FluidSink? {
        val storage = extractFluidStorage(level, pos, blockEntity)
        if (storage != null) {
            return storage
        }

        for (extractor in FLUID_SINK_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidSink(level: Level, entity: Entity): FluidSink? {
        val storage = extractFluidStorage(level, entity)
        if (storage != null) {
            return storage
        }
        for (extractor in FLUID_SINK_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidSinkFromUnknown(level: Level, obj: Any?): FluidSink? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractFluidSink(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractFluidSink(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractFluidSink(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
