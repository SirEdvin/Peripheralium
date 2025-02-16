package site.siredvin.peripheralium.xplat

import net.minecraft.core.NonNullList
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

fun interface MenuBuilder<M : AbstractContainerMenu> {
    fun build(id: Int, player: Inventory, data: FriendlyByteBuf): M
}

fun interface SavingFunction {
    fun toBytes(buf: FriendlyByteBuf)
}

interface CreativeTabProvider {
    fun makeIcon(): ItemStack
    fun appendItems(items: MutableList<ItemStack>): List<ItemStack>
}