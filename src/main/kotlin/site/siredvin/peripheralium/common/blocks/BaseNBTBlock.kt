package site.siredvin.peripheralium.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.Material
import site.siredvin.peripheralium.api.blockentities.ISyncingBlockEntity

abstract class BaseNBTBlock<T>(
    belongToTickingEntity: Boolean,
    properties: Properties = Properties.of(Material.METAL).strength(1f, 5f).sound(SoundType.METAL).noOcclusion(),
) :
    BaseTileEntityBlock<T>(belongToTickingEntity, properties) where T : BlockEntity, T : ISyncingBlockEntity {
    abstract fun createItemStack(): ItemStack

    open val savableProperties: List<Property<*>>
        get() = emptyList()

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ISyncingBlockEntity) {
            if (!level.isClientSide && !player.isCreative) {
                val itemstack: ItemStack = createItemStack()
                val internalData = blockEntity.saveInternalData(CompoundTag())
                if (!internalData.isEmpty) {
                    itemstack.addTagElement(INTERNAL_DATA_TAG, internalData)
                }
                val savableProperties: List<Property<*>> = savableProperties
                if (savableProperties.isNotEmpty() && !defaultBlockState().equals(state)) {
                    itemstack.addTagElement(BLOCK_STATE_TAG, NbtUtils.writeBlockState(state))
                }
                val itemDrop = ItemEntity(
                    level,
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    itemstack,
                )
                itemDrop.setDefaultPickUpDelay()
                level.addFreshEntity(itemDrop)
            }
        }
        super.playerWillDestroy(level, pos, state, player)
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, entity: LivingEntity?, stack: ItemStack) {
        var mutableState = state
        super.setPlacedBy(level, pos, mutableState, entity, stack)
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ISyncingBlockEntity) {
            if (!level.isClientSide) {
                val data = stack.tag
                if (data != null) {
                    if (data.contains(BLOCK_STATE_TAG)) {
                        val savedState: BlockState = NbtUtils.readBlockState(data.getCompound(BLOCK_STATE_TAG))
                        for (property in savableProperties) {
                            @Suppress("UNCHECKED_CAST")
                            property as Property<Comparable<Any>>
                            mutableState = mutableState.setValue(property, savedState.getValue(property) as Comparable<Any>)
                        }
                    }
                    if (data.contains(INTERNAL_DATA_TAG)) {
                        mutableState = blockEntity.loadInternalData(data.getCompound(INTERNAL_DATA_TAG), mutableState)
                        blockEntity.pushInternalDataChangeToClient(mutableState)
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
