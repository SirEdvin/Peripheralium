package site.siredvin.peripheralium.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.EntityTypeTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import site.siredvin.peripheralium.data.blocks.*
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.function.BiConsumer
import java.util.function.Consumer

class FabricDataGenerators : DataGeneratorEntrypoint {

    class DataGeneratorWrapper(private val fabricDataGenerator: FabricDataGenerator) : GeneratorSink {
        override fun <T : DataProvider> add(factory: (DataGenerator) -> T): T = fabricDataGenerator.addProvider(factory)

        override fun lootTable(tables: List<Pair<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>, LootContextParamSet>>) {
            tables.forEach {
                fabricDataGenerator.addProvider { out: FabricDataGenerator ->
                    object : SimpleFabricLootTableProvider(out, it.second) {
                        override fun accept(t: BiConsumer<ResourceLocation, LootTable.Builder>) {
                            it.first.accept(t)
                        }
                    }
                }
            }
        }

        override fun blockTags(
            modID: String,
            tags: Consumer<TagConsumer<Block>>,
        ): TagsProvider<Block> = object : BlockTagProvider(fabricDataGenerator) {
            override fun generateTags() {
                tags.accept { x ->
                    LibTagAppender(XplatRegistries.BLOCKS, this.getOrCreateRawBuilder(x))
                }
            }
        }

        override fun entityTags(
            modID: String,
            tags: Consumer<TagConsumer<EntityType<*>>>,
        ): TagsProvider<EntityType<*>> = object : EntityTypeTagProvider(fabricDataGenerator) {
            override fun generateTags() {
                tags.accept { x ->
                    LibTagAppender(XplatRegistries.ENTITY_TYPES, this.getOrCreateRawBuilder(x))
                }
            }
        }

        override fun itemTags(
            modID: String,
            tags: Consumer<ItemTagConsumer>,
            blocks: TagsProvider<Block>,
        ): TagsProvider<Item> = object : ItemTagProvider(fabricDataGenerator) {
            override fun generateTags() {
                val self = this
                tags.accept(object : ItemTagConsumer {
                    override fun copy(
                        block: TagKey<Block>,
                        item: TagKey<Item>,
                    ) {
                        this.copy(block, item)
                    }

                    override fun tag(tag: TagKey<Item>): LibTagAppender<Item> = LibTagAppender(XplatRegistries.ITEMS, self.getOrCreateRawBuilder(tag))
                })
            }
        }

        override fun models(
            blocks: Consumer<BlockModelGenerators>,
            items: Consumer<ItemModelGenerators>,
        ) {
            add { ModelProvider(fabricDataGenerator, blocks, items) }
        }
    }

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        LibDataProviders.add(DataGeneratorWrapper(fabricDataGenerator))
    }
}
