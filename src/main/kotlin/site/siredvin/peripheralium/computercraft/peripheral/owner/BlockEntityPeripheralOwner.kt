package site.siredvin.peripheralium.computercraft.peripheral.owner

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.common.blocks.GenericBlockEntityBlock
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import site.siredvin.peripheralium.util.DataStorageUtil
import site.siredvin.peripheralium.util.world.LibFakePlayer
import java.util.*

class BlockEntityPeripheralOwner<T>(val tileEntity: T) :
    BasePeripheralOwner() where T : BlockEntity, T : IPeripheralTileEntity {

    override val level: Level?
        get() = Objects.requireNonNull(tileEntity.level)
    override val pos: BlockPos
        get() = tileEntity.blockPos

    override val targetRepresentation: T
        get() = tileEntity

    override val facing: Direction
        get() = tileEntity.blockState.getValue(GenericBlockEntityBlock.FACING);

    override val owner: Player?
        get() = null
    override val dataStorage: CompoundTag
        get() = DataStorageUtil.getDataStorage(tileEntity)

    override fun markDataStorageDirty() {
        tileEntity.setChanged()
    }

    override fun <T> withPlayer(function: (LibFakePlayer) -> T, overwrittenDirection: Direction?): T {
        throw RuntimeException("Not implemented yet")
    }

    override val toolInMainHand: ItemStack
        get() = ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        throw RuntimeException("Not implemented yet")
    }

    override fun destroyUpgrade() {
        level!!.removeBlock(tileEntity.blockPos, false)
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean {
        return false
    }

    override fun move(level: Level, pos: BlockPos): Boolean {
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is BlockEntityPeripheralOwner<*>) return false
        return tileEntity == other.tileEntity
    }

    override fun hashCode(): Int {
        return tileEntity.hashCode()
    }
}