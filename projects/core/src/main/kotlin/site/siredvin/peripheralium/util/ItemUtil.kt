package site.siredvin.peripheralium.util

import net.minecraft.world.item.ItemStack

object ItemUtil {
//    private val POCKET_NORMAL: Item
//        get() = Registry.ITEM.get(ResourceLocation(ComputerCraftAPI.MOD_ID, "pocket_computer_normal"))
//
//    private val POCKET_ADVANCED: Item
//        get() = Registry.ITEM.get(ResourceLocation(ComputerCraftAPI.MOD_ID, "pocket_computer_advanced"))
//
//    private val TURTLE_NORMAL: Item
//        get() = Registry.ITEM.get(ResourceLocation(ComputerCraftAPI.MOD_ID, "turtle_normal"))
//
//    private val TURTLE_ADVANCED: Item
//        get() = Registry.ITEM.get(ResourceLocation(ComputerCraftAPI.MOD_ID, "turtle_advanced"))

    fun makeTurtle(upgrade: String): ItemStack {
        TODO()
//        val stack = ItemStack(TURTLE_NORMAL)
//        stack.orCreateTag.putString("RightUpgrade", upgrade)
//        return stack
    }

    fun makeAdvancedTurtle(upgrade: String): ItemStack {
        TODO()
//        val stack = ItemStack(TURTLE_ADVANCED)
//        stack.orCreateTag.putString("RightUpgrade", upgrade)
//        return stack
    }

    fun makePocket(upgrade: String): ItemStack {
        TODO()
//        val stack = ItemStack(POCKET_NORMAL)
//        stack.orCreateTag.putString("Upgrade", upgrade)
//        return stack
    }

    fun makeAdvancedPocket(upgrade: String): ItemStack {
        TODO()
//        val stack = ItemStack(POCKET_ADVANCED)
//        stack.orCreateTag.putString("Upgrade", upgrade)
//        return stack
    }
}