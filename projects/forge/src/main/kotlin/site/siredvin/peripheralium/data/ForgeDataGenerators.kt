package site.siredvin.peripheralium.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.data.blocks.GeneratorSink
import site.siredvin.peripheralium.data.blocks.ItemTagConsumer
import site.siredvin.peripheralium.data.blocks.TagConsumer
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
                event.lookupProvider
            )
        )
    }
    class ForgeGeneratorSink(private val generator: DataGenerator.PackGenerator, private val existingFiles: ExistingFileHelper, private val registries: CompletableFuture<HolderLookup.Provider>) : GeneratorSink {
        override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T {
            return generator.addProvider(factory)
        }

        override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
            add<LootTableProvider> { out: PackOutput ->
                LootTableProvider(out, setOf(), tables)
            }
        }

        override fun blockTags(tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> {
            TODO("Not yet implemented")
        }

        override fun itemTags(tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> {
            TODO("Not yet implemented")
        }
    }

}