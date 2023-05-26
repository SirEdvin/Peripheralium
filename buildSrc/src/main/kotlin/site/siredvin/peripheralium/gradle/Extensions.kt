package site.siredvin.peripheralium.gradle

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

/** Proxy method to avoid overload ambiguity. */
fun <T> Property<T>.setProvider(provider: Provider<out T>) = set(provider)