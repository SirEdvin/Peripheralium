package site.siredvin.peripheralium.ext

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

fun ResourceLocation.withPath(path: String): ResourceLocation = ResourceLocation(this.namespace, path)

fun ResourceLocation.withPrefix(prefix: String): ResourceLocation = this.withPath(prefix + this.path)

fun ResourceLocation.withSuffix(suffix: String): ResourceLocation = this.withPath(this.path + suffix)

fun ItemStack.copyWithCount(count: Int): ItemStack {
    val result = this.copy()
    result.count = count
    return result
}
