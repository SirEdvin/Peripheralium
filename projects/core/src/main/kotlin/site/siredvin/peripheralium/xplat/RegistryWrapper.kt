package site.siredvin.peripheralium.xplat

import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import java.util.Optional

interface RegistryWrapper<T> : Iterable<T> {
    fun getId(something: T): Int
    fun getKey(something: T): ResourceLocation
    fun get(location: ResourceLocation): T
    fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>>
    fun get(resourceKey: ResourceKey<T>): Optional<Holder<T>>

    fun tryGet(location: ResourceLocation): T?
    fun get(id: Int): T

    fun keySet(): Set<ResourceLocation>
}
