package site.siredvin.peripheralium.common.blockentities

import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import net.minecraft.nbt.CompoundTag
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import site.siredvin.peripheralium.api.blockentities.IOwnedBlockEntity
import site.siredvin.peripheralium.api.peripheral.IPeripheralProvider
import java.util.UUID

abstract class PeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState
) : BlockEntity(blockEntityType, blockPos, blockState), IPeripheralTileEntity, IPeripheralProvider<T>, IOwnedBlockEntity {
    // Peripheral logic
    final override var peripheralSettings: CompoundTag
        protected set

    protected var peripheral: T? = null
    protected var owningPlayer: Player? = null
    override var player: Player?
        get() = owningPlayer
        set(value) {owningPlayer = value}

    val connectedComputers: List<IComputerAccess>
        get() = if (peripheral == null) emptyList() else peripheral!!.connectedComputers

    init {
        peripheralSettings = CompoundTag()
    }

    override fun getPeripheral(side: Direction): T? {
        ensurePeripheralCreated(side)
        return peripheral!!
    }

    fun ensurePeripheralCreated(side: Direction) {
        if (peripheral == null) {
            peripheral = createPeripheral(side)
        }
    }

    protected abstract fun createPeripheral(side: Direction): T

    override fun saveAdditional(compound: CompoundTag) {
        super.saveAdditional(compound)
        if (!peripheralSettings.isEmpty) {
            compound.put(PERIPHERAL_DATA_TAG, peripheralSettings)
        }
        if (owningPlayer != null && owningPlayer!!.gameProfile.id != null)
            compound.putUUID(OWNER_PROFILE_TAG, owningPlayer!!.gameProfile.id)
    }

    override fun load(compound: CompoundTag) {
        if (compound.contains(PERIPHERAL_DATA_TAG)) peripheralSettings = compound.getCompound(PERIPHERAL_DATA_TAG)
        if (compound.contains(OWNER_PROFILE_TAG) && level != null && !level!!.isClientSide)
            owningPlayer = (level!! as ServerLevel).getPlayerByUUID(compound.getUUID(OWNER_PROFILE_TAG))
        super.load(compound)
    }

    override fun markSettingsChanged() {
        setChanged()
    }

    companion object {
        private const val PERIPHERAL_DATA_TAG = "peripheralData"
        private const val OWNER_PROFILE_TAG = "ownerProfile"
    }
}