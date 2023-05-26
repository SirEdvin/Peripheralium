package site.siredvin.testiralium.xplat

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

interface XplatToolkit {
    companion object {
        private var _IMPL: XplatToolkit? = null

        fun configure(impl: XplatToolkit) {
            _IMPL = impl
        }

        fun get(): XplatToolkit {
            if (_IMPL == null) {
                throw IllegalStateException("You should init Peripheral Platform first")
            }
            return _IMPL!!
        }

        fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
            return get().wrap(registry)
        }
    }

    fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T>
}