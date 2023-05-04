package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.*
import java.util.function.Predicate

object PeripheralPluginUtils {
    object TYPES {
        const val INVENTORY = "inventory"
        const val FLUID_STORAGE = "fluid_storage"
        const val ITEM_STORAGE = "item_storage"
        const val ENERGY_STORAGE = "energy_storage"
    }

    private object ITEM_QUERY_FIELD {
        const val name = "name"
        const val displayName = "displayName"
        const val tag = "tag"
        const val nbt = "nbt"
    }

    private val ALWAYS_ITEM_STACK_TRUE: Predicate<ItemStack> = Predicate { true }

    fun builtItemNamePredicate(name: String): Predicate<ItemStack> {
        val item = XplatRegistries.ITEMS.get(ResourceLocation(name))
        if (item == Items.AIR)
            throw LuaException("There is no item $name")
        return Predicate { it.`is`(item) }
    }

    fun builtItemDisplayNamePredicate(displayName: String): Predicate<ItemStack> {
        return Predicate { it.hoverName.string == displayName }
    }

    fun builtItemTagPredicate(tag: String): Predicate<ItemStack> {
        return Predicate { itemStack -> itemStack.tags.anyMatch{ it.location.toString() == tag } }
    }

    fun builtNBTPredicate(nbt: String): Predicate<ItemStack> {
        return Predicate {
            nbt == PeripheraliumPlatform.nbtHash(it.tag)
        }
    }

    fun itemQueryToPredicate(something: Any?): Predicate<ItemStack> {
        if (something == null)
            return ALWAYS_ITEM_STACK_TRUE
        if (something is String) {
            return builtItemNamePredicate(something)
        } else if (something is Map<*, *>) {
            var aggregated_predicate = ALWAYS_ITEM_STACK_TRUE
            if (something.contains(ITEM_QUERY_FIELD.name))
                aggregated_predicate = aggregated_predicate.and(builtItemNamePredicate(something[ITEM_QUERY_FIELD.name].toString()))
            if (something.contains(ITEM_QUERY_FIELD.displayName))
                aggregated_predicate = aggregated_predicate.and(builtItemDisplayNamePredicate(something[ITEM_QUERY_FIELD.displayName].toString()))
            if (something.contains(ITEM_QUERY_FIELD.tag))
                aggregated_predicate = aggregated_predicate.and(builtItemTagPredicate(something[ITEM_QUERY_FIELD.tag].toString()))
            if (something.contains(ITEM_QUERY_FIELD.nbt))
                aggregated_predicate = aggregated_predicate.and(builtNBTPredicate(something[ITEM_QUERY_FIELD.nbt].toString()))
            return aggregated_predicate
        }
        throw LuaException("Item query should be string or table")
    }
}