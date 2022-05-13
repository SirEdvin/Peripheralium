package site.siredvin.peripheralium.data

import com.google.gson.JsonObject
import net.minecraft.core.Registry
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.AbstractCookingRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCookingSerializer
import net.minecraft.world.level.ItemLike
import java.util.function.Consumer

class TweakedSmeltingRecipeBuilder(
    itemLike: ItemLike,
    private val ingredient: Ingredient,
    private val experience: Float,
    private val cookingTime: Int,
    private var serializer: SimpleCookingSerializer<*>,
    private var group: String? = null
) {
    private val result: Item

    init {
        result = itemLike.asItem()
    }

    companion object {
        fun cooking(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int, simpleCookingSerializer: SimpleCookingSerializer<*>): TweakedSmeltingRecipeBuilder {
            return TweakedSmeltingRecipeBuilder(itemLike, ingredient, experience, cookingTime, simpleCookingSerializer)
        }

        fun campfireCooking(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
        }

        fun blasting(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.BLASTING_RECIPE)
        }

        fun smelting(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.SMELTING_RECIPE)
        }

        fun smoking(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.SMOKING_RECIPE)
        }
    }

    fun group(string: String?): TweakedSmeltingRecipeBuilder {
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
        val var10004 = if (group == null) "" else group!!
        val var10005 = ingredient
        val var10006 = result
        val var10007 = experience
        val var10008 = cookingTime
        consumer.accept(
            Result(
                resourceLocation,
                var10004,
                var10005,
                var10006,
                var10007,
                var10008,
                serializer
            )
        )
    }

    class Result(
        private val id: ResourceLocation,
        private val group: String,
        private val ingredient: Ingredient,
        private val result: Item,
        private val experience: Float,
        private val cookingTime: Int,
        private val serializer: RecipeSerializer<out AbstractCookingRecipe?>
    ) : FinishedRecipe {
        override fun serializeRecipeData(jsonObject: JsonObject) {
            if (group.isNotEmpty()) {
                jsonObject.addProperty("group", group)
            }
            jsonObject.add("ingredient", ingredient.toJson())
            jsonObject.addProperty("result", Registry.ITEM.getKey(result).toString())
            jsonObject.addProperty("experience", experience)
            jsonObject.addProperty("cookingtime", cookingTime)
        }

        override fun getType(): RecipeSerializer<*> {
            return serializer
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