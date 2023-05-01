package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface Storage: TargetableStorage {
    fun getItems(): Iterator<ItemStack>
    fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack
}