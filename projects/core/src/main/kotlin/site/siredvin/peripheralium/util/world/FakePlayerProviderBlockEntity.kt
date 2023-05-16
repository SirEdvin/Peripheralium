package site.siredvin.peripheralium.util.world

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.lua.LuaException
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.api.blockentities.IOwnedBlockEntity
import site.siredvin.peripheralium.api.storage.ExtractorProxy
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*
import java.util.function.Function

object FakePlayerProviderBlockEntity {
    private val registeredPlayers: WeakHashMap<BlockEntity, ServerPlayer> =
        WeakHashMap<BlockEntity, ServerPlayer>()

    private fun getPlayer(blockEntity: BlockEntity, profile: GameProfile): ServerPlayer {
        if (blockEntity !is IOwnedBlockEntity || blockEntity.player == null)
            throw IllegalArgumentException("Cannot use fake player logic without owned block entity")
        var fake: ServerPlayer? = registeredPlayers[blockEntity]
        if (fake == null) {
            fake = PeripheraliumPlatform.createFakePlayer(blockEntity.player!!.level as ServerLevel, profile)
            registeredPlayers[blockEntity] = fake
        }
        return fake
    }

    private fun load(player: ServerPlayer, realPlayer: Player, storage: SlottedStorage?, overwrittenDirection: Direction? = null, skipInventory: Boolean = false) {
        val direction = overwrittenDirection ?: realPlayer.direction
        player.setLevel(realPlayer.level as ServerLevel)
        val position = realPlayer.blockPosition()
        // Player position
        val pitch: Float = if (direction == Direction.UP) -90f else if (direction == Direction.DOWN) 90f else 0f
        val yaw: Float =
            if (direction == Direction.SOUTH) 0f else if (direction == Direction.WEST) 90f else if (direction == Direction.NORTH) 180f else -90f
        val sideVec = direction.normal
        val a = direction.axis
        val ad = direction.axisDirection
        val x = if (a === Direction.Axis.X && ad == Direction.AxisDirection.NEGATIVE) -.5 else .5 + sideVec.x / 1.9
        val y = 0.5 + sideVec.y / 1.9
        val z = if (a === Direction.Axis.Z && ad == Direction.AxisDirection.NEGATIVE) -.5 else .5 + sideVec.z / 1.9
        player.moveTo(position.x + x, position.y + y, position.z + z, yaw, pitch)

        if (!skipInventory && storage != null) {

            // Player inventory
            val playerInventory: Inventory = player.inventory
            playerInventory.selected = 0

            // Copy primary items into player inventory and empty the rest
            val size = storage.size
            val fakeInventorySize = playerInventory.containerSize
            for (i in 0 until size) {
                playerInventory.setItem(i, storage.getItem(i))
            }
            if (fakeInventorySize > size)
                for (i in size until fakeInventorySize) {
                    playerInventory.setItem(i, ItemStack.EMPTY)
                }

            // Add properties
            val activeStack: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
            if (!activeStack.isEmpty) {
                player.attributes.addTransientAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
            }
        }
    }

    private fun unload(player: ServerPlayer, realPlayer: Player, storage: SlottedStorage?, skipInventory: Boolean = false) {
        val playerInventory: Inventory = player.inventory
        playerInventory.selected = 0

        // Remove properties
        val activeStack: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
        if (!activeStack.isEmpty) {
            player.attributes.removeAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
        }

        // Copy primary items into turtle inventory and then insert/drop the rest
        if (!skipInventory && storage != null) {
            val size: Int = storage.size
            val fakeInventorySize = playerInventory.containerSize
            playerInventory.selected = realPlayer.score
            for (i in 0 until size) {
                storage.storeItem(playerInventory.getItem(i), i, i)
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
            if (fakeInventorySize > size)
                for (i in size until fakeInventorySize) {
                    val remaining = playerInventory.getItem(i)
                    if (!remaining.isEmpty) {
                        StorageUtils.toInventoryOrToWorld(
                            remaining,
                            storage,
                            0,
                            realPlayer.blockPosition(),
                            realPlayer.level
                        )
                    }
                    playerInventory.setItem(i, ItemStack.EMPTY)
                }
        }
    }

    fun <T> withPlayer(blockEntity: BlockEntity, function: Function<ServerPlayer, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        if (blockEntity !is IOwnedBlockEntity || blockEntity.player == null)
            throw IllegalArgumentException("Cannot use fake player logic without owned block entity")
        val realPlayer = blockEntity.player
            ?: throw LuaException("Cannot init player for this block entity computer for some reason")
        val player: ServerPlayer =
            getPlayer(blockEntity, realPlayer.gameProfile ?: FakePlayerProxy.DUMMY_PROFILE)
        val storage = ExtractorProxy.extractStorage(blockEntity.level!!, blockEntity.blockPos) as? SlottedStorage
        if (!skipInventory && storage == null)
            throw IllegalArgumentException("Cannot init fake player with storage and with block entity without storage")
        load(player, realPlayer, storage, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
        val result = function.apply(player)
        unload(player, realPlayer, storage, skipInventory = skipInventory)
        return result
    }
}