package site.siredvin.peripheralium.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.EntityTypeTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import site.siredvin.peripheralium.data.blocks.*
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.function.BiConsumer
import java.util.function.Consumer

class FabricDataGenerators : DataGeneratorEntrypoint {

    class TabledFabricLootTableProvider(private val out: FabricDataOutput, private val table: LootTableProvider.SubProviderEntry) : SimpleFabricLootTableProvider(out, table.paramSet) {
        override fun accept(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
            table.provider().get().generate(consumer)
        }
    }

    class DataGeneratorWrapper(private val pack: FabricDataGenerator.Pack) : GeneratorSink {
        override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T {
            return pack.addProvider(factory)
        }

        override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
            tables.forEach {
                pack.addProvider { out: FabricDataOutput ->
                    object : SimpleFabricLootTableProvider(out, it.paramSet) {
                        override fun accept(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
                            it.provider.get().generate(consumer)
                        }
                    }
                }
            }
        }

        override fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> {
            return pack.addProvider { out, registries ->
                object : BlockTagProvider(out, registries) {
                    override fun addTags(registries: HolderLookup.Provider) {
                        tags.accept { x -> LibTagAppender(XplatRegistries.BLOCKS, getOrCreateRawBuilder(x)) }
                    }
                }
            }
        }

        override fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>> {
            return pack.addProvider { out, registries ->
                object : EntityTypeTagProvider(out, registries) {
                    override fun addTags(arg: HolderLookup.Provider) {
                        tags.accept { x -> LibTagAppender(XplatRegistries.ENTITY_TYPES, getOrCreateRawBuilder(x)) }
                    }
                }
            }
        }

        override fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> {
            return pack.addProvider { out, registries ->
                object : ItemTagProvider(out, registries, blocks as BlockTagProvider) {
                    override fun addTags(registries: HolderLookup.Provider) {
                        val self: ItemTagProvider = this
                        tags.accept(object : ItemTagConsumer {
                            override fun tag(tag: TagKey<Item>): LibTagAppender<Item> {
                                return LibTagAppender(XplatRegistries.ITEMS, getOrCreateRawBuilder(tag))
                            }

                            override fun copy(
                                block: TagKey<Block>,
                                item: TagKey<Item>,
                            ) {
                                self.copy(block, item)
                            }
                        })
                    }
                }
            }
        }

        override fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>) {
            add { ModelProvider(it, blocks, items) }
        }
    }

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        LibDataProviders.add(DataGeneratorWrapper(fabricDataGenerator.createPack()))
    }
}
