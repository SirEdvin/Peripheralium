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
        const val NAME = "name"
        const val DISPLAY_NAME = "displayName"
        const val TAG = "tag"
        const val NBT = "nbt"
    }

    private val ALWAYS_ITEM_STACK_TRUE: Predicate<ItemStack> = Predicate { true }

    fun builtItemNamePredicate(name: String): Predicate<ItemStack> {
        val item = XplatRegistries.ITEMS.get(ResourceLocation(name))
        if (item == Items.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(item) }
    }

    fun builtItemDisplayNamePredicate(displayName: String): Predicate<ItemStack> = Predicate { it.hoverName.string == displayName }

    fun builtItemTagPredicate(tag: String): Predicate<ItemStack> = Predicate { itemStack -> itemStack.tags.anyMatch { it.location.toString() == tag } }

    fun builtNBTPredicate(nbt: String): Predicate<ItemStack> = Predicate {
        nbt == PeripheraliumPlatform.nbtHash(it.tag)
    }

    fun itemQueryToPredicate(something: Any?): Predicate<ItemStack> {
        if (something == null) {
            return ALWAYS_ITEM_STACK_TRUE
        }
        if (something is String) {
            return builtItemNamePredicate(something)
        } else if (something is Map<*, *>) {
            var aggregatedPredicate = ALWAYS_ITEM_STACK_TRUE
            if (something.contains(ItemQueryField.NAME)) {
                aggregatedPredicate = aggregatedPredicate.and(builtItemNamePredicate(something[ItemQueryField.NAME].toString()))
            }
            if (something.contains(ItemQueryField.DISPLAY_NAME)) {
                aggregatedPredicate = aggregatedPredicate.and(builtItemDisplayNamePredicate(something[ItemQueryField.DISPLAY_NAME].toString()))
            }
            if (something.contains(ItemQueryField.TAG)) {
                aggregatedPredicate = aggregatedPredicate.and(builtItemTagPredicate(something[ItemQueryField.TAG].toString()))
            }
            if (something.contains(ItemQueryField.NBT)) {
                aggregatedPredicate = aggregatedPredicate.and(builtNBTPredicate(something[ItemQueryField.NBT].toString()))
            }
            return aggregatedPredicate
        }
        throw LuaException("Item query should be string or table")
    }
}
