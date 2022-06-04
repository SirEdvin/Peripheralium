package site.siredvin.peripheralium

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.api.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.peripheralium.api.blockentities.IObservingBlockEntity
import site.siredvin.peripheralium.common.configuration.ConfigHolder
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items


object Peripheralium: ModInitializer {
    const val MOD_ID = "peripheralium"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    override fun onInitialize() {
        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)
        Items.doSomething()
        Blocks.doSomething()
        registerHandlers()
    }

    fun registerHandlers() {
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(ServerBlockEntityEvents.Unload { blockEntity: BlockEntity, world: ServerLevel ->
            if (blockEntity is IObservingBlockEntity) {
                blockEntity.onChunkUnloaded()
            }
        })
    }
}