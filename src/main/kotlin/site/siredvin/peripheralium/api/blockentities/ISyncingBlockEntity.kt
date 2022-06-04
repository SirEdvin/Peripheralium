package site.siredvin.peripheralium.api.blockentities

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

interface ISyncingBlockEntity {
    fun saveInternalData(data: CompoundTag): CompoundTag
    fun loadInternalData(data: CompoundTag)
    fun pushInternalDataChangeToClient()
    fun pushInternalDataChangeToClient(state: BlockState)
    fun triggerRenderUpdate()
}