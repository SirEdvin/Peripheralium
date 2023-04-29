package site.siredvin.peripheralium.api.peripheral

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.computercraft.peripheral.ability.OperationAbility
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility

interface IPeripheralOwner {
    val name: String?
@dan200.computercraft.api.lua.LuaFunction get() {
        return owner?.customName.toString()
    }
    val targetRepresentation: Any?
        get() = null

    val level: Level?
    val pos: BlockPos
    val facing: Direction
    val owner: Player?
    val dataStorage: CompoundTag

    fun markDataStorageDirty()

    fun <T> withPlayer(function: (ServerPlayer) ->  T, overwrittenDirection: Direction? = null): T
    val toolInMainHand: ItemStack
    fun storeItem(stored: ItemStack): ItemStack
    fun destroyUpgrade()
    fun isMovementPossible(level: Level, pos: BlockPos): Boolean
    fun move(level: Level, pos: BlockPos): Boolean
    fun <T : IOwnerAbility> attachAbility(ability: PeripheralOwnerAbility<T>, abilityImplementation: T)
    fun <T : IOwnerAbility> getAbility(ability: PeripheralOwnerAbility<T>): T?
    val abilities: Collection<IOwnerAbility>
    fun attachOperation(vararg operations: IPeripheralOperation<*>, reduceRate: Double = 1.0) {
        val operationAbility = OperationAbility(this, reduceRate = reduceRate)
        attachAbility(PeripheralOwnerAbility.OPERATION, operationAbility)
        for (operation in operations) operationAbility.registerOperation(operation)
    }

    fun attachOperation(operations: Collection<IPeripheralOperation<*>>, reduceRate: Double = 1.0) {
        val operationAbility = OperationAbility(this, reduceRate = reduceRate)
        attachAbility(PeripheralOwnerAbility.OPERATION, operationAbility)
        for (operation in operations) operationAbility.registerOperation(operation)
    }
}