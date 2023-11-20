package site.siredvin.peripheralium.data

import com.google.common.collect.Lists
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.advancements.Advancement
import net.minecraft.core.Registry
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import java.util.function.Consumer

class TweakedShapelessRecipeBuilder(itemLike: ItemLike, private val count: Int) {
    private var result: Item
    private val ingredients: MutableList<Ingredient> = Lists.newArrayList()
    private val advancement = Advancement.Builder.advancement()
    private var group: String? = null

    init {
        result = itemLike.asItem()
    }

    companion object {
        fun shapeless(itemLike: ItemLike): TweakedShapelessRecipeBuilder {
            return TweakedShapelessRecipeBuilder(itemLike, 1)
        }

        fun shapeless(itemLike: ItemLike, i: Int): TweakedShapelessRecipeBuilder {
            return TweakedShapelessRecipeBuilder(itemLike, i)
        }
    }

    fun requires(tagKey: TagKey<Item>): TweakedShapelessRecipeBuilder {
        return this.requires(Ingredient.of(tagKey))
    }

    fun requires(itemLike: ItemLike): TweakedShapelessRecipeBuilder {
        return this.requires(itemLike, 1)
    }

    fun requires(itemLike: ItemLike?, i: Int): TweakedShapelessRecipeBuilder {
        for (j in 0 until i) {
            this.requires(Ingredient.of(*arrayOf(itemLike)))
        }
        return this
    }

    fun requires(ingredient: Ingredient): TweakedShapelessRecipeBuilder {
        return this.requires(ingredient, 1)
    }

    fun requires(ingredient: Ingredient, i: Int): TweakedShapelessRecipeBuilder {
        for (j in 0 until i) {
            ingredients.add(ingredient)
        }
        return this
    }

    fun group(string: String): TweakedShapelessRecipeBuilder {
        group = string
        return this
    }

    fun getResult(): Item {
        return result
    }

    fun save(consumer: Consumer<FinishedRecipe>) {
        this.save(consumer, Registry.ITEM.getKey(result))
    }

    fun save(consumer: Consumer<FinishedRecipe>, resourceLocation: ResourceLocation) {
        val var10004 = result
        val var10005 = count
        val var10006 = if (group == null) "" else group!!
        consumer.accept(
            Result(
                resourceLocation,
                var10004,
                var10005,
                var10006,
                ingredients,
            ),
        )
    }

    class Result(
        private val id: ResourceLocation,
        private val result: Item,
        private val count: Int,
        private val group: String,
        private val ingredients: List<Ingredient>,
    ) :
        FinishedRecipe {
        override fun serializeRecipeData(jsonObject: JsonObject) {
            if (group.isNotEmpty()) {
                jsonObject.addProperty("group", group)
            }
            val jsonArray = JsonArray()
            val var3: Iterator<*> = ingredients.iterator()
            while (var3.hasNext()) {
                val ingredient = var3.next() as Ingredient
                jsonArray.add(ingredient.toJson())
            }
            jsonObject.add("ingredients", jsonArray)
            val jsonObject2 = JsonObject()
            jsonObject2.addProperty("item", Registry.ITEM.getKey(result).toString())
            if (count > 1) {
                jsonObject2.addProperty("count", count)
            }
            jsonObject.add("result", jsonObject2)
        }

        override fun getType(): RecipeSerializer<*> {
            return RecipeSerializer.SHAPELESS_RECIPE
        }

        override fun getId(): ResourceLocation {
            return id
        }

        override fun serializeAdvancement(): JsonObject? {
            return null
        }

        override fun getAdvancementId(): ResourceLocation? {
            return null
        }
    }
}
