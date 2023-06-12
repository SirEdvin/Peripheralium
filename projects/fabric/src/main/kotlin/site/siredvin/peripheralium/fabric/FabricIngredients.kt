package site.siredvin.peripheralium.fabric

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.peripheralium.xplat.RecipeIngredients

object FabricIngredients : RecipeIngredients {
    override val redstone: Ingredient
        get() = Ingredient.of(ConventionalItemTags.REDSTONE_DUSTS)
    override val glowstoneDust: Ingredient
        get() = Ingredient.of(Items.GLOWSTONE_DUST)

    override val xpBottle: Ingredient
        get() = Ingredient.of(Items.EXPERIENCE_BOTTLE)
}
