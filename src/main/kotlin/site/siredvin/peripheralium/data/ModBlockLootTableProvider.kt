package site.siredvin.peripheralium.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import site.siredvin.peripheralium.common.setup.Blocks

class ModBlockLootTableProvider(dataGenerator: FabricDataGenerator) : FabricBlockLootTableProvider(dataGenerator) {
    override fun generateBlockLootTables() {
        dropSelf(Blocks.PERIPHERALIUM_BLOCK)
    }
}
