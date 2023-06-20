package site.siredvin.peripheralium.storages.item

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.minecraft.world.item.ItemStack

class SlidingIterator(private val iterator: Iterator<StorageView<ItemVariant>>) : Iterator<ItemStack> {
    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun next(): ItemStack {
        val view = iterator.next()
        return view.resource.toStack(view.amount.toInt())
    }
}
