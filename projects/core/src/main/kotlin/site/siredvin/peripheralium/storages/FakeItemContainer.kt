package site.siredvin.peripheralium.storages

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class FakeItemContainer(private var stack: ItemStack) : Container {
    override fun clearContent() {
        stack = ItemStack.EMPTY
    }

    override fun getContainerSize(): Int = 1

    override fun isEmpty(): Boolean = stack.isEmpty

    override fun getItem(i: Int): ItemStack = stack

    override fun removeItem(i: Int, j: Int): ItemStack = throw IllegalArgumentException("Should be called")

    override fun removeItemNoUpdate(i: Int): ItemStack = throw IllegalArgumentException("Should be called")

    override fun setItem(i: Int, itemStack: ItemStack): Unit = throw IllegalArgumentException("Should be called")

    override fun setChanged() {
    }

    override fun stillValid(player: Player): Boolean = true
}
