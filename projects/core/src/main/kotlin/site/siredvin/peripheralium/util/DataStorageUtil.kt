package site.siredvin.peripheralium.util

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.nbt.CompoundTag
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity

object DataStorageUtil {
    fun getDataStorage(access: ITurtleAccess, side: TurtleSide?): CompoundTag = access.getUpgradeNBTData(side)

    fun getDataStorage(tileEntity: IPeripheralTileEntity): CompoundTag = tileEntity.peripheralSettings

    fun getDataStorage(pocket: IPocketAccess): CompoundTag = pocket.upgradeNBTData
}
