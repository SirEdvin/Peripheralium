package site.siredvin.peripheralium.util.world

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.turtle.ITurtleAccess
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.storages.ContainerUtils
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*
import java.util.function.Function

object FakePlayerProviderTurtle {
    /*
    Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/integration/computercraft/FakePlayerProviderTurtle.java
     */
    private val registeredPlayers: WeakHashMap<ITurtleAccess, FakePlayerProxy> =
        WeakHashMap<ITurtleAccess, FakePlayerProxy>()

    private fun getPlayer(turtle: ITurtleAccess, profile: GameProfile): FakePlayerProxy {
        var fake: FakePlayerProxy? = registeredPlayers[turtle]
        if (fake == null) {
            fake = FakePlayerProxy(PeripheraliumPlatform.createFakePlayer(turtle.level as ServerLevel, profile))
            registeredPlayers[turtle] = fake
        }
        return fake
    }

    private fun load(player: ServerPlayer, turtle: ITurtleAccess, overwrittenDirection: Direction? = null, skipInventory: Boolean = false) {
        val direction = overwrittenDirection ?: turtle.direction
        player.setServerLevel(turtle.level as ServerLevel)
        val position = turtle.position
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
            val turtleInventory = turtle.inventory
            val size = turtleInventory.containerSize
            val largerSize = playerInventory.containerSize
            playerInventory.selected = turtle.selectedSlot
            for (i in 0 until size) {
                playerInventory.setItem(i, turtleInventory.getItem(i))
            }
            for (i in size until largerSize) {
                playerInventory.setItem(i, ItemStack.EMPTY)
            }

            // Add properties
            val activeStack: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
            if (!activeStack.isEmpty) {
                player.attributes.addTransientAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
            }
        }
    }

    private fun unload(player: ServerPlayer, turtle: ITurtleAccess, skipInventory: Boolean = false) {
        val playerInventory: Inventory = player.inventory
        playerInventory.selected = 0

        // Remove properties
        val activeStack: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
        if (!activeStack.isEmpty) {
            player.attributes.removeAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
        }

        if (!skipInventory) {
            // Copy primary items into turtle inventory and then insert/drop the rest
            val turtleInventory = turtle.inventory
            val size: Int = turtleInventory.containerSize
            val largerSize = playerInventory.containerSize
            playerInventory.selected = turtle.selectedSlot
            for (i in 0 until size) {
                turtleInventory.setItem(i, playerInventory.getItem(i))
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
            for (i in size until largerSize) {
                val remaining = playerInventory.getItem(i)
                if (!remaining.isEmpty) {
                    ContainerUtils.toInventoryOrToWorld(remaining, turtleInventory, 0, turtle.position, turtle.level)
                }
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
        }
    }

    fun <T> withPlayer(turtle: ITurtleAccess, function: Function<FakePlayerProxy, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        val player: FakePlayerProxy = getPlayer(turtle, turtle.owningPlayer ?: FakePlayerProxy.DUMMY_PROFILE)
        load(player.fakePlayer, turtle, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
        val result = function.apply(player)
        unload(player.fakePlayer, turtle, skipInventory = skipInventory)
        return result
    }
}
