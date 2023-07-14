package site.siredvin.peripheralium.ext

import dan200.computercraft.api.lua.IArguments
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.util.representation.LuaInterpretation

fun IArguments.getResourceLocation(index: Int): ResourceLocation {
    return LuaInterpretation.asID(this.getString(index))
}

fun IArguments.getItemStack(index: Int): ItemStack {
    return LuaInterpretation.asItemStack(this.get(index))
}
