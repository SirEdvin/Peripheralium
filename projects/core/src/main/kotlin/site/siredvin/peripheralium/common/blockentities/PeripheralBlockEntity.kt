package site.siredvin.peripheralium.common.blockentities

import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.api.blockentities.IOwnedBlockEntity
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.api.peripheral.IPeripheralProvider
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import java.util.UUID

abstract class PeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : BlockEntity(blockEntityType, blockPos, blockState), IPeripheralTileEntity, IPeripheralProvider<T>, IOwnedBlockEntity {
    // Peripheral logic
    final override var peripheralSettings: CompoundTag
        protected set

    protected var peripheral: T? = null
    protected var ownerPlayerUUID: UUID? = null
    override var player: Player?
        get() = ownerPlayerUUID?.let { level?.getPlayerByUUID(it) }
        set(value) { ownerPlayerUUID = value?.uuid }

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
        if (ownerPlayerUUID != null) {
            compound.putUUID(OWNER_PROFILE_TAG, ownerPlayerUUID!!)
        }
    }

    override fun load(compound: CompoundTag) {
        if (compound.contains(PERIPHERAL_DATA_TAG)) peripheralSettings = compound.getCompound(PERIPHERAL_DATA_TAG)
        if (compound.contains(OWNER_PROFILE_TAG)) {
            ownerPlayerUUID = compound.getUUID(OWNER_PROFILE_TAG)
        }
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
