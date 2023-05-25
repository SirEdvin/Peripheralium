package site.siredvin.peripheralium.util.world

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*
import java.util.function.Function

object FakePlayerProviderPocket {
    private val registeredPlayers: WeakHashMap<IPocketAccess, ServerPlayer> =
        WeakHashMap<IPocketAccess, ServerPlayer>()

    private fun getPlayer(pocket: IPocketAccess, profile: GameProfile): ServerPlayer {
        var fake: ServerPlayer? = registeredPlayers[pocket]
        if (fake == null) {
            fake = PeripheraliumPlatform.createFakePlayer(pocket.entity!!.level as ServerLevel, profile)
            registeredPlayers[pocket] = fake
        }
        return fake
    }

    private fun load(player: ServerPlayer, realPlayer: Player, overwrittenDirection: Direction? = null, skipInventory: Boolean = false) {
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

        if (!skipInventory) {
            // Player inventory
            val playerInventory: Inventory = player.inventory
            playerInventory.selected = 0

            // Copy primary items into player inventory and empty the rest
            val realPlayerInventory = realPlayer.inventory
            val size = realPlayerInventory.containerSize
            playerInventory.selected = realPlayer.inventory.selected
            for (i in 0 until size) {
                playerInventory.setItem(i, realPlayerInventory.getItem(i))
            }

            // Add properties
            val activeStack: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
            if (!activeStack.isEmpty) {
                player.attributes.addTransientAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
            }
        }
    }

    private fun unload(player: ServerPlayer, realPlayer: Player, skipInventory: Boolean = false) {
        val playerInventory: Inventory = player.inventory
        playerInventory.selected = 0

        // Remove properties
        val activeStack: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
        if (!activeStack.isEmpty) {
            player.attributes.removeAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
        }

        // Copy primary items into turtle inventory and then insert/drop the rest
        if (!skipInventory) {
            val realPlayerInventory = realPlayer.inventory
            val size: Int = realPlayerInventory.containerSize
            val largerSize = playerInventory.containerSize
            playerInventory.selected = realPlayer.score
            for (i in 0 until size) {
                realPlayerInventory.setItem(i, playerInventory.getItem(i))
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
        }
    }

    fun <T> withPlayer(pocket: IPocketAccess, function: Function<ServerPlayer, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        val realPlayer = pocket.entity as? Player
            ?: throw LuaException("Cannot init player for this pocket computer for some reason")
        val player: ServerPlayer =
            getPlayer(pocket, realPlayer.gameProfile ?: FakePlayerProxy.DUMMY_PROFILE)
        load(player, realPlayer, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
        val result = function.apply(player)
        unload(player, realPlayer, skipInventory = skipInventory)
        return result
    }
}
