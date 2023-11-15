package site.siredvin.peripheralium.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class ModDataGenerationEntrypoint : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        fabricDataGenerator.addProvider(ModRecipeProvider(fabricDataGenerator))
        fabricDataGenerator.addProvider(ModBlockLootTableProvider(fabricDataGenerator))
    }
}
