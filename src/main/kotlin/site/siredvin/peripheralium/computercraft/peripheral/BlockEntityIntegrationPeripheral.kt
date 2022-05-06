package site.siredvin.peripheralium.computercraft.peripheral

import net.minecraft.world.level.block.entity.BlockEntity

abstract class BlockEntityIntegrationPeripheral<T : BlockEntity>(val blockEntity: T) : IntegrationPeripheral() {

    override fun getTarget(): Any {
        return blockEntity
    }
}