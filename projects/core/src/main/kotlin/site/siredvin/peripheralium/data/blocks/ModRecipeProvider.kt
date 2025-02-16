package site.siredvin.peripheralium.data.blocks

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import site.siredvin.peripheralium.PeripheraliumCore.LOGGER
import java.io.IOException
import java.nio.file.Path
import java.util.function.Consumer

abstract class ModRecipeProvider(dataGenerator: DataGenerator) : RecipeProvider(dataGenerator) {

    abstract fun buildRecipes(consumer: Consumer<FinishedRecipe>)

    override fun run(cachedOutput: CachedOutput) {
        val resourceLocations = hashSetOf<ResourceLocation>()
        buildRecipes {
            if (!resourceLocations.add(it.id)) {
                throw IllegalStateException("Duplicate recipe " + it.id)
            } else {
                modSaveRecipe(cachedOutput, it.serializeRecipe(), this.recipePathProvider.json(it.id))
                val advancementJson = it.serializeAdvancement()
                if (advancementJson != null && it.advancementId != null) {
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    modSaveAdvancement(
                        cachedOutput,
                        advancementJson,
                        this.advancementPathProvider.json(it.advancementId),
                    )
                }
            }
        }
    }

    private fun modSaveRecipe(cachedOutput: CachedOutput?, jsonObject: JsonObject?, path: Path?) {
        try {
            DataProvider.saveStable(cachedOutput!!, jsonObject!!, path!!)
        } catch (exception: IOException) {
            LOGGER.error("Couldn't save recipe {}", path, exception)
        }
    }

    private fun modSaveAdvancement(cachedOutput: CachedOutput, jsonObject: JsonObject, path: Path) {
        try {
            DataProvider.saveStable(cachedOutput, jsonObject, path)
        } catch (exception: IOException) {
            LOGGER.error("Couldn't save recipe advancement {}", path, exception)
        }
    }
}
