package site.siredvin.peripheralium.api.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.computercraft.peripheral.ability.OperationAbility
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility
import java.util.function.BiConsumer
import java.util.function.Consumer

interface IPeripheralOwner {
    val name: String?
        @LuaFunction get() {
            return owner?.customName.toString()
        }
    val targetRepresentation: Any?
        get() = null

    val level: Level?
    val pos: BlockPos
    val facing: Direction
    val owner: Player?
    val dataStorage: CompoundTag
    val storage: SlottedStorage?

    fun markDataStorageDirty()

    fun <T> withPlayer(function: (ServerPlayer) -> T, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T
    val toolInMainHand: ItemStack
    fun storeItem(stored: ItemStack): ItemStack
    fun destroyUpgrade()
    fun isMovementPossible(level: Level, pos: BlockPos): Boolean
    fun move(level: Level, pos: BlockPos): Boolean
    fun <T : IOwnerAbility> attachAbility(ability: IPeripheralOwnerAbility<T>, abilityImplementation: T)
    fun <T : IOwnerAbility> getAbility(ability: IPeripheralOwnerAbility<T>): T?
    val abilities: Collection<IOwnerAbility>

    @Throws(LuaException::class)
    fun <T> withOperation(
        operation: IPeripheralOperation<T>,
        context: T,
        method: IPeripheralFunction<T, MethodResult>,
        check: IPeripheralCheck<T>? = null,
        successCallback: Consumer<T>? = null,
        failCallback: BiConsumer<MethodResult, OperationAbility.FailReason>? = null,
    ): MethodResult {
        val operationAbility = getAbility(PeripheralOwnerAbility.OPERATION)
            ?: throw IllegalArgumentException("Owner doesn't have ability to store operations logic, which is very strange!")
        return operationAbility.performOperation(operation, context, check, method, successCallback, failCallback)
    }
}
