package site.siredvin.peripheralium.api.storage

import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

interface MovableSlottedStorage: MovableStorage, SlottedStorage {
    fun moveTo(to: TargetableStorage, limit: Int, fromSlot: Int = -1,  toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int
    fun moveFrom(from: Storage, limit: Int, toSlot: Int = -1, fromSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int

    override fun moveTo(to: TargetableStorage, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        return moveTo(to, limit, -1, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: Storage,
        limit: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>
    ): Int {
        return moveFrom(from, limit, -1, fromSlot, takePredicate)
    }
}