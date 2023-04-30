package site.siredvin.peripheralium.xplat

import net.minecraft.world.item.crafting.Ingredient

interface RecipeIngredients {
    companion object {
        private var _IMPL: RecipeIngredients? = null

        fun configure(impl: RecipeIngredients) {
            _IMPL = impl
        }

        fun get(): RecipeIngredients {
            if (_IMPL == null)
                throw IllegalStateException("You should init recipe ingredients first")
            return _IMPL!!
        }
    }

    val redstone: Ingredient
    val glowstoneDust: Ingredient
}