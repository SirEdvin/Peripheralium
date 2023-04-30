package site.siredvin.peripheralium.fabric

import net.minecraft.network.Connection
import net.minecraft.network.PacketSendListener
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.game.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl

class FakeNetHandler(player: ServerPlayer): ServerGamePacketListenerImpl(player.level.server!!, CONNECTION, player) {

    companion object {
        private val CONNECTION: Connection = Connection(PacketFlow.CLIENTBOUND)
    }

    override fun tick() {}

    override fun resetPosition() {}

    override fun disconnect(textComponent: Component) {}

    override fun handlePlayerInput(packet: ServerboundPlayerInputPacket) {}

    override fun handleMoveVehicle(packet: ServerboundMoveVehiclePacket) {}

    override fun handleAcceptTeleportPacket(packet: ServerboundAcceptTeleportationPacket) {}

    override fun handleRecipeBookSeenRecipePacket(packet: ServerboundRecipeBookSeenRecipePacket) {}

    override fun handleRecipeBookChangeSettingsPacket(packet: ServerboundRecipeBookChangeSettingsPacket) {}

    override fun handleSeenAdvancements(packet: ServerboundSeenAdvancementsPacket) {}

    override fun handleCustomCommandSuggestions(packet: ServerboundCommandSuggestionPacket) {}

    override fun handleSetCommandBlock(packet: ServerboundSetCommandBlockPacket) {}

    override fun handleSetCommandMinecart(packet: ServerboundSetCommandMinecartPacket) {}

    override fun handlePickItem(packet: ServerboundPickItemPacket) {}

    override fun handleRenameItem(packet: ServerboundRenameItemPacket) {}

    override fun handleSetBeaconPacket(packet: ServerboundSetBeaconPacket) {}

    override fun handleSetStructureBlock(packet: ServerboundSetStructureBlockPacket) {}

    override fun handleSetJigsawBlock(packet: ServerboundSetJigsawBlockPacket) {}

    override fun handleJigsawGenerate(packet: ServerboundJigsawGeneratePacket) {}

    override fun handleSelectTrade(packet: ServerboundSelectTradePacket) {}

    override fun handleEditBook(packet: ServerboundEditBookPacket) {}

    override fun handleEntityTagQuery(packet: ServerboundEntityTagQuery) {}

    override fun handleBlockEntityTagQuery(packet: ServerboundBlockEntityTagQuery) {}

    override fun handleMovePlayer(packet: ServerboundMovePlayerPacket) {}

    override fun handlePlayerAction(packet: ServerboundPlayerActionPacket) {}

    override fun handleUseItemOn(packet: ServerboundUseItemOnPacket) {}

    override fun handleUseItem(packet: ServerboundUseItemPacket) {}

    override fun handleTeleportToEntityPacket(packet: ServerboundTeleportToEntityPacket) {}

    override fun handleResourcePackResponse(packet: ServerboundResourcePackPacket) {}

    override fun handlePaddleBoat(packet: ServerboundPaddleBoatPacket) {}

    override fun handlePong(packet: ServerboundPongPacket) {}

    override fun onDisconnect(reason: Component) {}

    override fun ackBlockChangesUpTo(i: Int) {}

    override fun send(packet: Packet<*>, packetSendListener: PacketSendListener?) {
        super.send(packet, packetSendListener)
    }

    override fun handleSetCarriedItem(packet: ServerboundSetCarriedItemPacket) {}

    override fun handleChat(packet: ServerboundChatPacket) {}

    override fun handleChatCommand(serverboundChatCommandPacket: ServerboundChatCommandPacket) {}

    override fun handleChatSessionUpdate(serverboundChatSessionUpdatePacket: ServerboundChatSessionUpdatePacket) {}

    override fun handleChatAck(serverboundChatAckPacket: ServerboundChatAckPacket) {}

    override fun handleAnimate(packet: ServerboundSwingPacket) {}

    override fun handlePlayerCommand(packet: ServerboundPlayerCommandPacket) {}

    override fun addPendingMessage(playerChatMessage: PlayerChatMessage) {}

    override fun handleInteract(packet: ServerboundInteractPacket) {}

    override fun handleClientCommand(packet: ServerboundClientCommandPacket) {}

    override fun handleContainerClose(packet: ServerboundContainerClosePacket) {}

    override fun handleContainerClick(packet: ServerboundContainerClickPacket) {}

    override fun handlePlaceRecipe(packet: ServerboundPlaceRecipePacket) {}

    override fun handleContainerButtonClick(packet: ServerboundContainerButtonClickPacket) {}

    override fun handleSetCreativeModeSlot(packet: ServerboundSetCreativeModeSlotPacket) {}

    override fun handleSignUpdate(packet: ServerboundSignUpdatePacket) {}

    override fun handleKeepAlive(packet: ServerboundKeepAlivePacket) {}

    override fun handlePlayerAbilities(packet: ServerboundPlayerAbilitiesPacket) {}

    override fun handleClientInformation(packet: ServerboundClientInformationPacket) {}

    override fun handleCustomPayload(packet: ServerboundCustomPayloadPacket) {}

    override fun handleChangeDifficulty(packet: ServerboundChangeDifficultyPacket) {}

    override fun handleLockDifficulty(packet: ServerboundLockDifficultyPacket) {}
}