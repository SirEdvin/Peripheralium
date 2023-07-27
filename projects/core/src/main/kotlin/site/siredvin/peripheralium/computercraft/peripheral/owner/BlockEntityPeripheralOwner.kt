package site.siredvin.peripheralium.computercraft.peripheral.owner

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.DirectionProperty
import site.siredvin.peripheralium.api.blockentities.IOwnedBlockEntity
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import site.siredvin.peripheralium.common.blocks.FacingBlockEntityBlock
import site.siredvin.peripheralium.storages.item.ItemStorageExtractor
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import site.siredvin.peripheralium.util.DataStorageUtil
import site.siredvin.peripheralium.util.world.FakePlayerProviderBlockEntity
import site.siredvin.peripheralium.util.world.FakePlayerProxy
import java.util.*

class BlockEntityPeripheralOwner<T>(private val tileEntity: T, private val facingProperty: DirectionProperty = FacingBlockEntityBlock.FACING) :
    BasePeripheralOwner() where T : BlockEntity, T : IPeripheralTileEntity {

    override val level: Level?
        get() = Objects.requireNonNull(tileEntity.level)
    override val pos: BlockPos
        get() = tileEntity.blockPos

    override val targetRepresentation: T
        get() = tileEntity

    override val facing: Direction
        get() = tileEntity.blockState.getValue(facingProperty)

    override val owner: Player?
        get() = (tileEntity as? IOwnedBlockEntity)?.player
    override val dataStorage: CompoundTag
        get() = DataStorageUtil.getDataStorage(tileEntity)

    override val storage: SlottedItemStorage? by lazy {
        ItemStorageExtractor.extractStorage(tileEntity.level!!, tileEntity.blockPos, tileEntity) as? SlottedItemStorage
    }

    override fun markDataStorageDirty() {
        tileEntity.setChanged()
    }

    override fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T {
        if (tileEntity !is IOwnedBlockEntity) {
            throw IllegalArgumentException("Cannot perform player logic without owned block entity")
        }
        return FakePlayerProviderBlockEntity.withPlayer(tileEntity, function, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
    }

    override val toolInMainHand: ItemStack
        get() = ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        if (storage == null) {
            return stored
        }
        return storage!!.storeItem(stored)
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
        if (this === other) return true
        if (other !is BlockEntityPeripheralOwner<*>) return false

        if (tileEntity != other.tileEntity) return false
        return facingProperty == other.facingProperty
    }

    override fun hashCode(): Int {
        var result = tileEntity.hashCode()
        result = 31 * result + facingProperty.hashCode()
        return result
    }
}
