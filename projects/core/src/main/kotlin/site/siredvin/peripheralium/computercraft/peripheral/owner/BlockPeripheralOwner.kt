package site.siredvin.peripheralium.computercraft.peripheral.owner

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.DirectionProperty
import site.siredvin.peripheralium.common.blocks.FacingBlockEntityBlock
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import site.siredvin.peripheralium.util.world.FakePlayerProxy

open class BlockPeripheralOwner(protected val blockPos: BlockPos, protected val blockLevel: Level, protected val facingProperty: DirectionProperty = FacingBlockEntityBlock.FACING) : BasePeripheralOwner() {

    override val level: Level
        get() = blockLevel
    override val pos: BlockPos
        get() = blockPos

    override val targetRepresentation: BlockPos
        get() = blockPos

    override val facing: Direction
        get() {
            val state = blockLevel.getBlockState(blockPos)
            if (!state.hasProperty(facingProperty)) return Direction.NORTH
            return state.getValue(facingProperty)
        }

    override val owner: Player?
        get() = null
    override val dataStorage: CompoundTag by lazy {
        CompoundTag()
    }

    override val storage: SlottedItemStorage?
        get() = null

    override fun markDataStorageDirty() {
    }

    override fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T = throw IllegalArgumentException("Cannot perform player logic for block owner")

    override val toolInMainHand: ItemStack
        get() = ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack = stored

    override fun destroyUpgrade() {
        level.removeBlock(blockPos, false)
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean = false

    override fun move(level: Level, pos: BlockPos): Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as BlockPeripheralOwner

        if (blockPos != other.blockPos) return false
        if (blockLevel != other.blockLevel) return false
        if (facingProperty != other.facingProperty) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + blockPos.hashCode()
        result = 31 * result + blockLevel.hashCode()
        result = 31 * result + facingProperty.hashCode()
        return result
    }
}
