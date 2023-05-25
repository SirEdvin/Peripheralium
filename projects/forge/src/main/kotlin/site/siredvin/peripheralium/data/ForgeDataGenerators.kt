package site.siredvin.peripheralium.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.tags.EntityTypeTagsProvider
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.BlockTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.data.blocks.GeneratorSink
import site.siredvin.peripheralium.data.blocks.ItemTagConsumer
import site.siredvin.peripheralium.data.blocks.LibTagAppender
import site.siredvin.peripheralium.data.blocks.TagConsumer
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

@Mod.EventBusSubscriber(modid = PeripheraliumCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeDataGenerators {
    @SubscribeEvent
    fun genData(event: GatherDataEvent) {
        val generator = event.generator
        LibDataProviders.add(
            ForgeGeneratorSink(
                generator.getVanillaPack(true),
                event.existingFileHelper,
                event.lookupProvider,
            ),
        )
    }
    class ForgeGeneratorSink(private val generator: DataGenerator.PackGenerator, private val existingFiles: ExistingFileHelper, private val registries: CompletableFuture<HolderLookup.Provider>) : GeneratorSink {
        override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T {
            return generator.addProvider(factory)
        }

        override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
            add { out: PackOutput ->
                LootTableProvider(out, setOf(), tables)
            }
        }

        override fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> {
            return add { out ->
                object : BlockTagsProvider(out, registries, modID, existingFiles) {
                    override fun addTags(registries: HolderLookup.Provider) {
                        tags.accept { x -> LibTagAppender(XplatRegistries.BLOCKS, getOrCreateRawBuilder(x)) }
                    }
                }
            }
        }

        override fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>> {
            return add { out ->
                object : EntityTypeTagsProvider(out, registries, modID, existingFiles) {
                    override fun addTags(arg: HolderLookup.Provider) {
                        tags.accept { x -> LibTagAppender(XplatRegistries.ENTITY_TYPES, getOrCreateRawBuilder(x)) }
                    }
                }
            }
        }

        override fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> {
            return add { out ->
                object :
                    ItemTagsProvider(out, registries, blocks.contentsGetter(), modID, existingFiles) {
                    override fun addTags(registries: HolderLookup.Provider) {
                        val self: ItemTagsProvider = this
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
    }
}
