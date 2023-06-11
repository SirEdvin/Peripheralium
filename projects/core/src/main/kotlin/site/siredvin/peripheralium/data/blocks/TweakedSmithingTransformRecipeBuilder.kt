package site.siredvin.peripheralium.data.blocks

import com.google.gson.JsonObject
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.function.Consumer

class TweakedSmithingTransformRecipeBuilder(
    private val type: RecipeSerializer<*>,
    private val template: Ingredient,
    private val base: Ingredient,
    private val addition: Ingredient,
    private val result: Item,
) {

    companion object {
        fun smithingTransform(template: Ingredient, base: Ingredient, addition: Ingredient, item: Item): TweakedSmithingTransformRecipeBuilder {
            return TweakedSmithingTransformRecipeBuilder(RecipeSerializer.SMITHING_TRANSFORM, template, base, addition, item)
        }
    }

    fun save(consumer: Consumer<FinishedRecipe>) {
        this.save(consumer, XplatRegistries.ITEMS.getKey(result))
    }

    fun save(consumer: Consumer<FinishedRecipe>, resourceLocation: ResourceLocation) {
        consumer.accept(
            Result(
                resourceLocation,
                type,
                template,
                base,
                addition,
                result,
            ),
        )
    }

    class Result(
        private val id: ResourceLocation,
        private val type: RecipeSerializer<*>,
        private val template: Ingredient,
        private val base: Ingredient,
        private val addition: Ingredient,
        private val result: Item,
    ) :
        FinishedRecipe {
        override fun serializeRecipeData(jsonObject: JsonObject) {
            jsonObject.add("template", template.toJson())
            jsonObject.add("base", base.toJson())
            jsonObject.add("addition", addition.toJson())
            val jsonObject2 = JsonObject()
            jsonObject2.addProperty("item", XplatRegistries.ITEMS.getKey(result).toString())
            jsonObject.add("result", jsonObject2)
        }

        override fun getId(): ResourceLocation {
            return id
        }

        override fun getType(): RecipeSerializer<*> {
            return type
        }

        override fun serializeAdvancement(): JsonObject? {
            return null
        }

        override fun getAdvancementId(): ResourceLocation? {
            return null
        }
    }
}
