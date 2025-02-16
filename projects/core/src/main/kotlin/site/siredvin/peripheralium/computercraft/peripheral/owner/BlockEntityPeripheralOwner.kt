package site.siredvin.peripheralium.computercraft.peripheral.owner

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.DirectionProperty
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import site.siredvin.peripheralium.common.blocks.FacingBlockEntityBlock
import site.siredvin.peripheralium.util.DataStorageUtil

class BlockEntityPeripheralOwner<T>(blockEntity: T, facingProperty: DirectionProperty = FacingBlockEntityBlock.FACING) : RawBlockEntityPeripheralOwner<T>(blockEntity, facingProperty) where T : BlockEntity, T : IPeripheralTileEntity {
    override val dataStorage: CompoundTag
        get() = DataStorageUtil.getDataStorage(blockEntity)

    override fun markDataStorageDirty() {
        blockEntity.setChanged()
    }
}
