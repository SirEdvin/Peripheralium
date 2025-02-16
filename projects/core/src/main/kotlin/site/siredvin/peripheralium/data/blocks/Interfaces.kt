package site.siredvin.peripheralium.data.blocks

import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import site.siredvin.peripheralium.xplat.RegistryWrapper
import java.util.function.BiConsumer
import java.util.function.Consumer

interface GeneratorSink {
    fun <T : DataProvider> add(factory: (DataGenerator) -> T): T
    fun lootTable(tables: List<Pair<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>, LootContextParamSet>>)
    fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block>

    fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>>
    fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item>
    fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>)
}

fun interface TagConsumer<T> {
    fun tag(tag: TagKey<T>): LibTagAppender<T>
}

class LibTagAppender<T>(private val registry: RegistryWrapper<T>, private val builder: TagBuilder) {
    fun add(`object`: T): LibTagAppender<T> {
        builder.addElement(registry.getKey(`object`))
        return this
    }

    @SafeVarargs
    fun add(vararg objects: T): LibTagAppender<T> {
        for (`object` in objects) add(`object`)
        return this
    }

    fun addTag(tag: TagKey<T>): LibTagAppender<T> {
        builder.addTag(tag.location())
        return this
    }
}

interface ItemTagConsumer : TagConsumer<Item> {
    // TODO: copy function doesn't work quite well for fabric, so I should address or remove it
    fun copy(block: TagKey<Block>, item: TagKey<Item>)
}
