package site.siredvin.peripheralium.data.blocks

import com.google.gson.JsonElement
import net.minecraft.Util
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.blockstates.BlockStateGenerator
import net.minecraft.data.models.model.DelegatedModel
import net.minecraft.data.models.model.ModelLocationUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * This is a copy from https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.19.x/projects/common/src/main/java/dan200/computercraft/data/ModelProvider.java
 * A copy of [net.minecraft.data.models.ModelProvider] which accepts a custom generator.
 *
 *
 * Please don't sue me Mojang. Or at least make these changes to vanilla before doing so!
 */
class ModelProvider(
    output: PackOutput,
    private val blocks: Consumer<BlockModelGenerators>,
    private val items: Consumer<ItemModelGenerators>,
) : DataProvider {
    private val blockStatePath: PackOutput.PathProvider
    private val modelPath: PackOutput.PathProvider

    init {
        blockStatePath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates")
        modelPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models")
    }

    override fun run(output: CachedOutput): CompletableFuture<*> {
        val blockStates: MutableMap<Block, BlockStateGenerator> = HashMap()
        val addBlockState = Consumer { generator: BlockStateGenerator ->
            val block = generator.block
            check(!blockStates.containsKey(block)) { "Duplicate blockstate definition for $block" }
            blockStates[block] = generator
        }
        val models: MutableMap<ResourceLocation, Supplier<JsonElement>> = HashMap()
        val addModel = BiConsumer { id: ResourceLocation, contents: Supplier<JsonElement> ->
            check(!models.containsKey(id)) { "Duplicate model definition for $id" }
            models[id] = contents
        }
        val explicitItems: MutableSet<Item> = HashSet()
        blocks.accept(BlockModelGenerators(addBlockState, addModel) { e: Item -> explicitItems.add(e) })
        items.accept(ItemModelGenerators(addModel))
        for (block in XplatRegistries.BLOCKS) {
            if (!blockStates.containsKey(block)) continue
            val item = Item.BY_BLOCK[block]
            if (item == null || explicitItems.contains(item)) continue
            val model = ModelLocationUtils.getModelLocation(item)
            if (!models.containsKey(model)) {
                models[model] = DelegatedModel(ModelLocationUtils.getModelLocation(block))
            }
        }
        val futures: MutableList<CompletableFuture<*>> = ArrayList()
        saveCollection(
            output,
            futures,
            blockStates,
        ) { blockStatePath.json(XplatRegistries.BLOCKS.getKey(it)) }
        saveCollection(output, futures, models, modelPath::json)
        return Util.sequenceFailFast(futures)
    }

    private fun <T> saveCollection(
        output: CachedOutput,
        futures: MutableList<CompletableFuture<*>>,
        items: Map<T, Supplier<JsonElement>>,
        getLocation: Function<T, Path>,
    ) {
        for ((key, value) in items) {
            val path = getLocation.apply(key)
            futures.add(DataProvider.saveStable(output, value.get(), path))
        }
    }

    override fun getName(): String {
        return "Block State Definitions"
    }
}
