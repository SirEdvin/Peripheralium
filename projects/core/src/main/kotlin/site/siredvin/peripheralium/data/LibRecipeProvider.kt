package site.siredvin.peripheralium.data

import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.data.blocks.TweakedShapedRecipeBuilder
import site.siredvin.peripheralium.data.blocks.TweakedShapelessRecipeBuilder
import site.siredvin.peripheralium.data.blocks.TweakedSmeltingRecipeBuilder
import site.siredvin.peripheralium.xplat.RecipeIngredients
import java.util.function.Consumer

class LibRecipeProvider(output: PackOutput) : RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe>) {
        val ingredients = RecipeIngredients.get()

        TweakedShapelessRecipeBuilder.shapeless(Items.PERIPHERALIUM_BLEND.get())
            .requires(ingredients.redstone)
            .requires(ingredients.glowstoneDust)
            .save(consumer)

        TweakedShapelessRecipeBuilder.shapeless(Blocks.PERIPHERALIUM_BLOCK.get())
            .requires(Items.PERIPHERALIUM_DUST.get(), 9)
            .save(consumer)

        TweakedShapelessRecipeBuilder.shapeless(Items.PERIPHERALIUM_DUST.get(), 9)
            .requires(Blocks.PERIPHERALIUM_BLOCK.get().asItem())
            .save(consumer, ResourceLocation("peripheralium:peripheralium_block_uncraft"))

        TweakedSmeltingRecipeBuilder.smelting(Ingredient.of(Items.PERIPHERALIUM_BLEND.get()), Items.PERIPHERALIUM_DUST.get(), 0.7f, 200)
            .save(consumer, ResourceLocation("peripheralium:peripheralium_dust_smelting"))

        TweakedShapedRecipeBuilder.shaped(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), 4)
            .define('P', Ingredient.of(Items.PERIPHERALIUM_DUST.get()))
            .define('X', ingredients.xpBottle)
            .pattern("PPP")
            .pattern("PXP")
            .pattern("P P")
            .save(consumer)
    }
}
