package site.siredvin.peripheralium.common.blockentities

import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheralTile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity

abstract class PeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : BlockEntity(blockEntityType, blockPos, blockState), IPeripheralTileEntity, IPeripheralTile {
    // Peripheral logic
    final override var peripheralSettings: CompoundTag
        protected set

    protected var peripheral: T? = null

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
    }

    override fun load(compound: CompoundTag) {
        if (compound.contains(PERIPHERAL_DATA_TAG)) peripheralSettings = compound.getCompound(PERIPHERAL_DATA_TAG)
        super.load(compound)
    }

    override fun markSettingsChanged() {
        setChanged()
    }

    companion object {
        private const val PERIPHERAL_DATA_TAG = "peripheralData"
    }
}
