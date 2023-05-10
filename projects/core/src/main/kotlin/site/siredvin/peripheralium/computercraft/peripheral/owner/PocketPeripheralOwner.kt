package site.siredvin.peripheralium.computercraft.peripheral.owner

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.ContainerUtils
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.util.DataStorageUtil
import site.siredvin.peripheralium.util.world.FakePlayerProviderPocket

class PocketPeripheralOwner(val pocket: IPocketAccess) : BasePeripheralOwner() {
    override val level: Level?
        get() {
            val owner = pocket.entity ?: return null
            return owner.commandSenderWorld
        }
    override val pos: BlockPos
        get() {
            val owner = pocket.entity ?: return BlockPos(0, 0, 0)
            return owner.blockPosition()
        }
    override val facing: Direction
        get() {
            val owner = pocket.entity ?: return Direction.NORTH
            return owner.direction
        }
    override val owner: Player?
        get() = pocket.entity as? Player

    override val dataStorage: CompoundTag
        get() = pocket.let { DataStorageUtil.getDataStorage(it) }

    override val storage: SlottedStorage?
        get() = owner?.inventory?.let { TargetableContainer(it) }

    override fun markDataStorageDirty() {
        pocket.updateUpgradeNBTData()
    }

    override fun <T> withPlayer(function: (ServerPlayer) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T {
        return FakePlayerProviderPocket.withPlayer(pocket, function, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
    }

    override val toolInMainHand: ItemStack
        get() = owner?.mainHandItem ?: ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        val player = owner ?: return stored
        return ContainerUtils.storeItem(player.inventory, stored)
    }

    override fun destroyUpgrade() {
        throw RuntimeException("Not implemented yet")
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean {
        return false
    }

    override fun move(level: Level, pos: BlockPos): Boolean {
        return false
    }
}