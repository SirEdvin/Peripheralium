package site.siredvin.peripheralium

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.xplat.LibClientHooks


@Environment(EnvType.CLIENT)
object FabricPeripheraliumClient: ClientModInitializer {
    override fun onInitializeClient() {
    }
}