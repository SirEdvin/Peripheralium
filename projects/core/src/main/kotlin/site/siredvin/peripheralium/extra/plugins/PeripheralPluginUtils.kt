package site.siredvin.peripheralium.extra.plugins

import dan200.computercraft.api.lua.LuaException
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.function.Predicate

object PeripheralPluginUtils {
    object Type {
        const val INVENTORY = "inventory"
        const val FLUID_STORAGE = "fluid_storage"
        const val ITEM_STORAGE = "item_storage"
        const val ENERGY_STORAGE = "energy_storage"
        const val PLUGGABLE = "pluggable"
    }

    private object ItemQueryField {
        const val name = "name"
        const val displayName = "displayName"
        const val tag = "tag"
        const val nbt = "nbt"
    }

    private val ALWAYS_ITEM_STACK_TRUE: Predicate<ItemStack> = Predicate { true }

    fun builtItemNamePredicate(name: String): Predicate<ItemStack> {
        val item = XplatRegistries.ITEMS.get(ResourceLocation(name))
        if (item == Items.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(item) }
    }

    fun builtItemDisplayNamePredicate(displayName: String): Predicate<ItemStack> {
        return Predicate { it.hoverName.string == displayName }
    }

    fun builtItemTagPredicate(tag: String): Predicate<ItemStack> {
        return Predicate { itemStack -> itemStack.tags.anyMatch { it.location.toString() == tag } }
    }

    fun builtNBTPredicate(nbt: String): Predicate<ItemStack> {
        return Predicate {
            nbt == PeripheraliumPlatform.nbtHash(it.tag)
        }
    }

    fun itemQueryToPredicate(something: Any?): Predicate<ItemStack> {
        if (something == null) {
            return ALWAYS_ITEM_STACK_TRUE
        }
        if (something is String) {
            return builtItemNamePredicate(something)
        } else if (something is Map<*, *>) {
            var aggregated_predicate = ALWAYS_ITEM_STACK_TRUE
            if (something.contains(ItemQueryField.name)) {
                aggregated_predicate = aggregated_predicate.and(builtItemNamePredicate(something[ItemQueryField.name].toString()))
            }
            if (something.contains(ItemQueryField.displayName)) {
                aggregated_predicate = aggregated_predicate.and(builtItemDisplayNamePredicate(something[ItemQueryField.displayName].toString()))
            }
            if (something.contains(ItemQueryField.tag)) {
                aggregated_predicate = aggregated_predicate.and(builtItemTagPredicate(something[ItemQueryField.tag].toString()))
            }
            if (something.contains(ItemQueryField.nbt)) {
                aggregated_predicate = aggregated_predicate.and(builtNBTPredicate(something[ItemQueryField.nbt].toString()))
            }
            return aggregated_predicate
        }
        throw LuaException("Item query should be string or table")
    }
}
