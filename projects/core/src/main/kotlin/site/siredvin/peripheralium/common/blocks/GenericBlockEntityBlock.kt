package site.siredvin.peripheralium.common.blocks

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.peripheralium.util.BlockUtil
import java.util.function.Supplier

class GenericBlockEntityBlock<T : BlockEntity>(
    blockEntityTypeSup: Supplier<BlockEntityType<T>>,
    isRotatable: Boolean,
    belongToTickingEntity: Boolean = false,
    properties: Properties = BlockUtil.defaultProperties(),
) : FacingBlockEntityBlock<T>(blockEntityTypeSup, isRotatable, belongToTickingEntity, properties)
