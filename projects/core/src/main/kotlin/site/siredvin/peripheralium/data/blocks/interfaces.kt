package site.siredvin.peripheralium.data.blocks

import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.xplat.RegistryWrapper
import java.util.function.Consumer


interface GeneratorSink {
    fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T
    fun lootTable(tables: List<LootTableProvider.SubProviderEntry>)
    fun blockTags(tags: Consumer<TagConsumer<Block>>): TagsProvider<Block>
    fun itemTags(tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item>
}


interface TagConsumer<T> {
    fun tag(tag: TagKey<T>?): TagAppender<T>?
}


class TagAppender<T>(private val registry: RegistryWrapper<T>, private val builder: TagBuilder) {
    fun add(`object`: T): TagAppender<T> {
        builder.addElement(registry.getKey(`object`))
        return this
    }

    @SafeVarargs
    fun add(vararg objects: T): TagAppender<T> {
        for (`object` in objects) add(`object`)
        return this
    }

    fun addTag(tag: TagKey<T>): TagAppender<T> {
        builder.addTag(tag.location())
        return this
    }
}

interface ItemTagConsumer : TagConsumer<Item?> {
    fun copy(block: TagKey<Block?>?, item: TagKey<Item?>?)
}
