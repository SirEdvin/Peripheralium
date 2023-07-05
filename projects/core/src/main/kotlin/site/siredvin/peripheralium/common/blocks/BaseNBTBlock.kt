package site.siredvin.peripheralium.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import site.siredvin.peripheralium.api.blockentities.ISyncingBlockEntity
import site.siredvin.peripheralium.util.BlockUtil
import site.siredvin.peripheralium.xplat.XplatRegistries

abstract class BaseNBTBlock<T>(
    belongToTickingEntity: Boolean,
    properties: Properties = BlockUtil.defaultProperties(),
) : BaseTileEntityBlock<T>(belongToTickingEntity, properties) where T : BlockEntity, T : ISyncingBlockEntity {
    abstract fun createItemStack(): ItemStack

    open fun prepareItemStack(blockEntity: ISyncingBlockEntity, state: BlockState): ItemStack {
        val stack: ItemStack = createItemStack()
        val internalData = blockEntity.saveInternalData(CompoundTag())
        if (!internalData.isEmpty) {
            stack.addTagElement(INTERNAL_DATA_TAG, internalData)
        }
        val savableProperties: List<Property<*>> = savableProperties
        if (savableProperties.isNotEmpty() && !defaultBlockState().equals(state)) {
            stack.addTagElement(BLOCK_STATE_TAG, NbtUtils.writeBlockState(state))
        }
        return stack
    }

    open val savableProperties: List<Property<*>>
        get() = emptyList()

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ISyncingBlockEntity) {
            if (!level.isClientSide && !player.isCreative) {
                val stack = prepareItemStack(blockEntity, state)
                val itemDrop = ItemEntity(
                    level,
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    stack,
                )
                itemDrop.setDefaultPickUpDelay()
                level.addFreshEntity(itemDrop)
            }
        }
        super.playerWillDestroy(level, pos, state, player)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun setPlacedBy(level: Level, pos: BlockPos, initialState: BlockState, entity: LivingEntity?, stack: ItemStack) {
        var state = initialState
        super.setPlacedBy(level, pos, state, entity, stack)
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ISyncingBlockEntity) {
            if (!level.isClientSide) {
                val data = stack.tag
                if (data != null) {
                    if (data.contains(BLOCK_STATE_TAG)) {
                        val savedState: BlockState = NbtUtils.readBlockState(XplatRegistries.BLOCKS, data.getCompound(BLOCK_STATE_TAG))
                        for (property in savableProperties) {
                            @Suppress("UNCHECKED_CAST")
                            property as Property<Comparable<Any>>
                            state = state.setValue(property, savedState.getValue(property) as Comparable<Any>)
                        }
                    }
                    if (data.contains(INTERNAL_DATA_TAG)) {
                        state = blockEntity.loadInternalData(data.getCompound(INTERNAL_DATA_TAG), state)
                        blockEntity.pushInternalDataChangeToClient(state)
                    }
                }
            }
        }
    }

    companion object {
        const val INTERNAL_DATA_TAG = "internalData"
        const val BLOCK_STATE_TAG = "blockState"
    }
}
