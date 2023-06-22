package site.siredvin.peripheralium.storages.energy

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

object EnergyStorageExtractor {
    fun interface EnergySinkExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): EnergySink?
    }

    fun interface EnergyStorageExtractor {
        fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): EnergyStorage?
    }

    fun interface EnergySinkEntityExtractor {
        fun extract(level: Level, entity: Entity): EnergySink?
    }

    fun interface EnergyStorageEntityExtractor {
        fun extract(level: Level, entity: Entity): EnergyStorage?
    }

    fun interface EnergyStorageItemExtractor {
        fun extract(level: Level, stack: ItemStack): EnergyStorage?
    }

    fun interface EnergySinkItemExtractor {
        fun extract(level: Level, stack: ItemStack): EnergyStorage?
    }

    private val ENERGY_SINK_EXTRACTORS: MutableList<EnergySinkExtractor> = mutableListOf()
    private val ENERGY_STORAGE_EXTRACTORS: MutableList<EnergyStorageExtractor> = mutableListOf()

    private val ENERGY_SINK_ENTITY_EXTRACTORS: MutableList<EnergySinkEntityExtractor> = mutableListOf()
    private val ENERGY_STORAGE_ENTITY_EXTRACTORS: MutableList<EnergyStorageEntityExtractor> = mutableListOf()

    private val ENERGY_SINK_ITEM_EXTRACTORS: MutableList<EnergySinkItemExtractor> = mutableListOf()
    private val ENERGY_STORAGE_ITEM_EXTRACTORS: MutableList<EnergyStorageItemExtractor> = mutableListOf()

    fun addEnergySinkExtractor(extractor: EnergySinkExtractor) {
        ENERGY_SINK_EXTRACTORS.add(extractor)
    }

    fun addEnergyStorageExtractor(extractor: EnergyStorageExtractor) {
        ENERGY_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addEnergySinkExtractor(extractor: EnergySinkEntityExtractor) {
        ENERGY_SINK_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addEnergyStorageExtractor(extractor: EnergyStorageEntityExtractor) {
        ENERGY_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addEnergySinkExtractor(extractor: EnergySinkItemExtractor) {
        ENERGY_SINK_ITEM_EXTRACTORS.add(extractor)
    }

    fun addEnergyStorageExtractor(extractor: EnergyStorageItemExtractor) {
        ENERGY_STORAGE_ITEM_EXTRACTORS.add(extractor)
    }

    fun extractEnergyStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): EnergyStorage? {
        for (extractor in ENERGY_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }

        if (blockEntity != null) {
            val turtle = PeripheraliumPlatform.getTurtleAccess(blockEntity)
            if (turtle != null) return TurtleEnergyStorage(turtle)
        }

        return null
    }

    fun extractEnergyStorage(level: Level, entity: Entity): EnergyStorage? {
        for (extractor in ENERGY_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergyStorage(level: Level, stack: ItemStack): EnergyStorage? {
        for (extractor in ENERGY_STORAGE_ITEM_EXTRACTORS) {
            val result = extractor.extract(level, stack)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergyStorageFromUnknown(level: Level, obj: Any?): EnergyStorage? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractEnergyStorage(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractEnergyStorage(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractEnergyStorage(level, obj)
        }
        if (obj is ItemStack) {
            return extractEnergyStorage(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractEnergySink(level: Level, pos: BlockPos, blockEntity: BlockEntity?): EnergySink? {
        val storage = extractEnergyStorage(level, pos, blockEntity)
        if (storage != null) {
            return storage
        }

        for (extractor in ENERGY_SINK_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergySink(level: Level, entity: Entity): EnergySink? {
        val storage = extractEnergyStorage(level, entity)
        if (storage != null) {
            return storage
        }
        for (extractor in ENERGY_SINK_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergySink(level: Level, stack: ItemStack): EnergySink? {
        val storage = extractEnergyStorage(level, stack)
        if (storage != null) {
            return storage
        }
        for (extractor in ENERGY_SINK_ITEM_EXTRACTORS) {
            val result = extractor.extract(level, stack)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergySinkFromUnknown(level: Level, obj: Any?): EnergySink? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractEnergySink(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractEnergySink(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractEnergySink(level, obj)
        }
        if (obj is ItemStack) {
            return extractEnergySink(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
