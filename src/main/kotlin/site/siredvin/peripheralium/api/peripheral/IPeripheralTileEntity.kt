package site.siredvin.peripheralium.api.peripheral

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

interface IPeripheralTileEntity {
    val peripheralSettings: CompoundTag
    fun markSettingsChanged()
    fun handleTick(level: Level, pos: BlockPos, state: BlockState) {}
}