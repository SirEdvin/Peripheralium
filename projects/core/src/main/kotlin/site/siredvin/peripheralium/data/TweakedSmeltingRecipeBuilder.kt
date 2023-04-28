package site.siredvin.peripheralium.data

import com.google.gson.JsonObject
import net.minecraft.core.Registry
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.AbstractCookingRecipe
import net.minecraft.world.item.crafting.BlastingRecipe
import net.minecraft.world.item.crafting.CampfireCookingRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.SimpleCookingSerializer
import net.minecraft.world.item.crafting.SmeltingRecipe
import net.minecraft.world.item.crafting.SmokingRecipe
import net.minecraft.world.level.ItemLike
import site.siredvin.peripheralium.api.PeripheraliumPlatform
import java.util.function.Consumer

class TweakedSmeltingRecipeBuilder<T: AbstractCookingRecipe>(
    itemLike: ItemLike,
    private val ingredient: Ingredient,
    private val experience: Float,
    private val cookingTime: Int,
    private var serializer: RecipeSerializer<T>,
    private var group: String? = null
) {
    private val result: Item

    init {
        result = itemLike.asItem()
    }

    companion object {
        fun <T: AbstractCookingRecipe> cooking(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int, simpleCookingSerializer: RecipeSerializer<T>): TweakedSmeltingRecipeBuilder<T> {
            return TweakedSmeltingRecipeBuilder(itemLike, ingredient, experience, cookingTime, simpleCookingSerializer)
        }

        fun campfireCooking(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder<CampfireCookingRecipe> {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
        }

        fun blasting(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder<BlastingRecipe> {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.BLASTING_RECIPE)
        }

        fun smelting(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder<SmeltingRecipe> {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.SMELTING_RECIPE)
        }

        fun smoking(ingredient: Ingredient, itemLike: ItemLike, experience: Float, cookingTime: Int): TweakedSmeltingRecipeBuilder<SmokingRecipe> {
            return cooking(ingredient, itemLike, experience, cookingTime, RecipeSerializer.SMOKING_RECIPE)
        }
    }

    fun group(string: String?): TweakedSmeltingRecipeBuilder<T> {
        group = string
        return this
    }

    fun getResult(): Item {
        return result
    }

    fun save(consumer: Consumer<FinishedRecipe>) {
        this.save(consumer, PeripheraliumPlatform.getKey(result))
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
            jsonObject.addProperty("result", PeripheraliumPlatform.getKey(result).toString())
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