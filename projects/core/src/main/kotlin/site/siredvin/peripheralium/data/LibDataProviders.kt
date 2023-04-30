package site.siredvin.peripheralium.data

import site.siredvin.peripheralium.data.blocks.GeneratorSink


object LibDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add {
            LibRecipeProvider(it)
        }
        generator.lootTable(LibLootTableProvider.getTables())
    }

}