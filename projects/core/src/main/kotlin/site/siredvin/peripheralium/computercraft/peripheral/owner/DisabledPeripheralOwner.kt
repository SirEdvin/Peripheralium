package site.siredvin.peripheralium.computercraft.peripheral.owner

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.SlottedStorage

class DisabledPeripheralOwner : BasePeripheralOwner() {
    override val level: Level?
        get() = null
    override val pos: BlockPos
        get() = BlockPos.ZERO
    override val facing: Direction
        get() = Direction.EAST
    override val owner: Player?
        get() = null
    override val dataStorage: CompoundTag
        get() = CompoundTag()

    override val storage: SlottedStorage?
        get() = null

    override fun markDataStorageDirty() {
    }

    override fun <T> withPlayer(
        function: (ServerPlayer) -> T,
        overwrittenDirection: Direction?,
        skipInventory: Boolean,
    ): T {
        throw RuntimeException("Really no code should use this, this is disabled owner")
    }

    override val toolInMainHand: ItemStack
        get() = ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        throw RuntimeException("Really no code should use this, this is disabled owner")
    }

    override fun destroyUpgrade() {
        throw RuntimeException("Really no code should use this, this is disabled owner")
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean {
        throw RuntimeException("Really no code should use this, this is disabled owner")
    }

    override fun move(level: Level, pos: BlockPos): Boolean {
        throw RuntimeException("Really no code should use this, this is disabled owner")
    }
}
