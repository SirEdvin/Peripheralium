package site.siredvin.peripheralium.storage

import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.StorageUtils
import java.util.function.Predicate

class ItemHandlerWrapper(private val handler: IItemHandler) : SlottedStorage {
    override fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack {
        var slidingLimit = limit
        var slidingItemStack = ItemStack.EMPTY
        for (currentSlot in startSlot..endSlot) {
            val tryExtractedStack = handler.extractItem(currentSlot, limit, true)
            if (tryExtractedStack.isEmpty) {
                continue
            }
            if (!predicate.test(tryExtractedStack)) {
                continue
            }
            val extractedStack = handler.extractItem(currentSlot, limit, false)
            if (slidingItemStack.isEmpty) {
                slidingItemStack = extractedStack
                // So, update actual limit to have a little more sense
                slidingLimit = minOf(slidingItemStack.maxStackSize, limit)
                slidingLimit -= extractedStack.count
            } else {
                if (StorageUtils.canMerge(slidingItemStack, extractedStack)) {
                    val extractedCount = extractedStack.count
                    val remainExtracted = StorageUtils.inplaceMerge(slidingItemStack, extractedStack)
                    if (!remainExtracted.isEmpty) {
                        handler.insertItem(currentSlot, remainExtracted, false)
                    }
                    slidingLimit -= extractedCount - remainExtracted.count
                } else {
                    handler.insertItem(currentSlot, extractedStack, false)
                }
            }
            if (slidingLimit <= 0) {
                break
            }
        }
        return slidingItemStack
    }

    override fun getItem(slot: Int): ItemStack {
        return handler.getStackInSlot(slot)
    }

    override fun canPlaceItem(slot: Int, item: ItemStack): Boolean {
        return true
    }

    override fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack {
        var slidingItemStack = stack
        for (currentSlot in startSlot..endSlot) {
            slidingItemStack = handler.insertItem(currentSlot, slidingItemStack, false)
            if (slidingItemStack.isEmpty) {
                break
            }
        }
        return slidingItemStack
    }

    override fun setChanged() {
    }

    override val size: Int
        get() = handler.slots
}
