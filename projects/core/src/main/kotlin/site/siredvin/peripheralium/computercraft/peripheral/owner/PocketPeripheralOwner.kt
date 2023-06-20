package site.siredvin.peripheralium.computercraft.peripheral.owner

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility
import site.siredvin.peripheralium.computercraft.peripheral.ability.PocketFuelAbility
import site.siredvin.peripheralium.storages.ContainerUtils
import site.siredvin.peripheralium.storages.ContainerWrapper
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import site.siredvin.peripheralium.util.DataStorageUtil
import site.siredvin.peripheralium.util.world.FakePlayerProviderPocket
import site.siredvin.peripheralium.util.world.FakePlayerProxy

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

    override val storage: SlottedItemStorage?
        get() = owner?.inventory?.let { ContainerWrapper(it) }

    override fun markDataStorageDirty() {
        pocket.updateUpgradeNBTData()
    }

    override fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T {
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

    fun attachFuel(foodFuelPrice: Int = 1000, maxFuelConsumptionLevel: Int = 1): PocketPeripheralOwner {
        attachAbility(PeripheralOwnerAbility.FUEL, PocketFuelAbility(this, foodFuelPrice, maxFuelConsumptionLevel))
        return this
    }
}
