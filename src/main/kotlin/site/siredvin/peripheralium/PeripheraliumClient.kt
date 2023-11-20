package site.siredvin.peripheralium

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.peripheralium.api.blockentities.IObservingBlockEntity

@Environment(EnvType.CLIENT)
object PeripheraliumClient : ClientModInitializer {
    override fun onInitializeClient() {
        registerEvents()
    }

    fun registerEvents() {
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(
            ClientBlockEntityEvents.Unload { blockEntity: BlockEntity, _: ClientLevel ->
                if (blockEntity is IObservingBlockEntity) {
                    blockEntity.onChunkUnloaded()
                }
            },
        )
    }
}
