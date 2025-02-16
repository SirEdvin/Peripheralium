package site.siredvin.peripheralium.ext

import dan200.computercraft.api.lua.IArguments
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.util.representation.LuaInterpretation

fun IArguments.getResourceLocation(index: Int): ResourceLocation = LuaInterpretation.asID(this.getString(index))

fun IArguments.getItemStack(index: Int): ItemStack = LuaInterpretation.asItemStack(this.get(index))

fun IArguments.getBlockState(index: Int): BlockState = LuaInterpretation.asBlockState(this.getTable(index))
