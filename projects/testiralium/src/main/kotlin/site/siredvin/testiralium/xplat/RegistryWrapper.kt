package site.siredvin.testiralium.xplat

import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceLocation

interface RegistryWrapper<T> : HolderGetter<T> {
    fun getId(something: T): Int
    fun getKey(something: T): ResourceLocation
    fun get(location: ResourceLocation): T

    fun tryGet(location: ResourceLocation): T?
    fun get(id: Int): T
}
