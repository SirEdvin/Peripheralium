package site.siredvin.peripheralium.api.blockentities

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

interface ISyncingBlockEntity {
    fun saveInternalData(data: CompoundTag): CompoundTag
    fun loadInternalData(data: CompoundTag, state: BlockState? = null): BlockState
    fun pushInternalDataChangeToClient(state: BlockState? = null)
    fun triggerRenderUpdate()
}