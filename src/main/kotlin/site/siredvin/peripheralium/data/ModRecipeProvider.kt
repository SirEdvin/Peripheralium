package site.siredvin.peripheralium.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SmeltingRecipe
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import java.util.function.Consumer

class ModRecipeProvider(dataGenerator: FabricDataGenerator) : FabricRecipeProvider(dataGenerator) {
    override fun generateRecipes(consumer: Consumer<FinishedRecipe>) {
        TweakedShapelessRecipeBuilder.shapeless(Items.PERIPHERALIUM_BLEND)
            .requires(net.minecraft.world.item.Items.REDSTONE)
            .requires(net.minecraft.world.item.Items.GLOWSTONE_DUST)
            .save(consumer)

        TweakedShapelessRecipeBuilder.shapeless(Blocks.PERIPHERALIUM_BLOCK)
            .requires(Items.PERIPHERALIUM_DUST, 9)
            .save(consumer)

        TweakedShapelessRecipeBuilder.shapeless(Items.PERIPHERALIUM_DUST, 9)
            .requires(Blocks.PERIPHERALIUM_BLOCK.asItem())
            .save(consumer, ResourceLocation("peripheralium:peripheralium_block_uncraft"))

        TweakedSmeltingRecipeBuilder.smelting(Ingredient.of(Items.PERIPHERALIUM_BLEND), Items.PERIPHERALIUM_DUST, 0.7f, 200)
            .save(consumer)
    }
}