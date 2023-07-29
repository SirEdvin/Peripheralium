package site.siredvin.peripheralium.computercraft.peripheral.owner

import dan200.computercraft.api.lua.LuaException
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.api.blockentities.IOwnedBlockEntity
import site.siredvin.peripheralium.api.peripheral.IPeripheralTileEntity
import site.siredvin.peripheralium.ext.toVec3
import site.siredvin.peripheralium.storages.item.ItemStorageExtractor
import site.siredvin.peripheralium.storages.item.SlottedItemStorage
import site.siredvin.peripheralium.util.DataStorageUtil
import site.siredvin.peripheralium.util.world.FakePlayerProviderEntity
import site.siredvin.peripheralium.util.world.FakePlayerProxy

class EntityProxyPeripheralOwner<T>(private val tileEntity: T, private val entity: Entity) :
    BasePeripheralOwner() where T : BlockEntity, T : IPeripheralTileEntity {
    override val level: Level
        get() = entity.level()
    override val pos: BlockPos
        get() = entity.blockPosition()
    override val facing: Direction
        get() = Direction.fromYRot(entity.yRot.toDouble())
    override val owner: Player?
        get() = (tileEntity as? IOwnedBlockEntity)?.player
    override val dataStorage: CompoundTag
        get() = DataStorageUtil.getDataStorage(tileEntity)
    override val storage: SlottedItemStorage? by lazy {
        ItemStorageExtractor.extractStorage(entity.level(), entity) as? SlottedItemStorage
    }

    override fun markDataStorageDirty() {
        tileEntity.setChanged()
    }

    override fun <T> withPlayer(
        function: (FakePlayerProxy) -> T,
        overwrittenDirection: Direction?,
        skipInventory: Boolean,
    ): T {
        if (tileEntity !is IOwnedBlockEntity) {
            throw IllegalArgumentException("Cannot perform player logic without owned block entity")
        }
        val player = tileEntity.player as? ServerPlayer ?: throw LuaException("Cannot correctly find player")
        return FakePlayerProviderEntity.withPlayer(entity, player, function, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
    }

    override val toolInMainHand: ItemStack
        get() = (entity as? LivingEntity)?.getItemInHand(InteractionHand.MAIN_HAND) ?: ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        if (storage == null) {
            return stored
        }
        return storage!!.storeItem(stored)
    }

    override fun destroyUpgrade() {
        level.removeBlock(tileEntity.blockPos, false)
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean {
        return withPlayer({ _ ->
            if (level.isOutsideBuildHeight(pos)) {
                return@withPlayer false
            }
            if (!level.isInWorldBounds(pos)) {
                return@withPlayer false
            }
            if (!level.isLoaded(pos)) {
                return@withPlayer false
            }
            return@withPlayer level.worldBorder.isWithinBounds(pos)
        })
    }

    override fun move(level: Level, pos: BlockPos): Boolean {
        entity.moveTo(pos.toVec3())
        return true
    }

    override fun hashCode(): Int {
        var result = tileEntity.hashCode()
        result = 31 * result + entity.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityProxyPeripheralOwner<*>) return false
        if (!super.equals(other)) return false

        if (tileEntity != other.tileEntity) return false
        if (entity != other.entity) return false

        return true
    }
}
