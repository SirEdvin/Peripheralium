package site.siredvin.peripheralium.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import site.siredvin.peripheralium.data.blocks.GeneratorSink
import site.siredvin.peripheralium.data.blocks.ItemTagConsumer
import site.siredvin.peripheralium.data.blocks.TagConsumer
import java.util.function.BiConsumer
import java.util.function.Consumer


class FabricDataGenerators: DataGeneratorEntrypoint {

    class TabledFabricLootTableProvider(private val out: FabricDataOutput, private val table: LootTableProvider.SubProviderEntry): SimpleFabricLootTableProvider(out, table.paramSet) {
        override fun accept(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
            table.provider().get().generate(consumer)
        }

    }
    
    class DataGeneratorWrapper(private val pack: FabricDataGenerator.Pack): GeneratorSink {
        override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T {
            return pack.addProvider(factory)
        }

        override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
            tables.forEach {
                pack.addProvider { out: FabricDataOutput -> object: SimpleFabricLootTableProvider(out, it.paramSet) {
                    override fun accept(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
                        it.provider.get().generate(consumer)
                    }
                } }
            }
        }

        override fun blockTags(tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> {
            TODO("Not yet implemented")
        }

        override fun itemTags(tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> {
            TODO("Not yet implemented")
        }

    }
    
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        LibDataProviders.add(DataGeneratorWrapper(fabricDataGenerator.createPack()))
    }
}