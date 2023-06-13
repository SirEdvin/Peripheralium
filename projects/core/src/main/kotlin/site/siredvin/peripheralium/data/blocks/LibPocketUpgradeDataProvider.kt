package site.siredvin.peripheralium.data.blocks

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.function.Consumer
import java.util.function.Supplier

abstract class LibPocketUpgradeDataProvider(output: PackOutput, serializers: List<Supplier<PocketUpgradeSerialiser<*>>>) : PocketUpgradeDataProvider(output) {
    private val serializers: List<ResourceLocation> = serializers.map {
        XplatRegistries.POCKET_SERIALIZERS.getKey(it.get())
    }
    override fun addUpgrades(addUpgrade: Consumer<Upgrade<PocketUpgradeSerialiser<*>>>) {
        val touchedSerializers = mutableSetOf<ResourceLocation>()
        registerUpgrades {
            addUpgrade.accept(it)
            touchedSerializers.add(XplatRegistries.POCKET_SERIALIZERS.getKey(it.serialiser))
        }
        val missedSerializers = serializers.filter { !touchedSerializers.contains(it) }
        if (missedSerializers.isNotEmpty()) {
            throw IllegalArgumentException("Some serializers don't have default items: $missedSerializers")
        }
    }

    fun <V : IPocketUpgrade> simpleWithCustomItem(serialiser: PocketUpgradeSerialiser<V>, item: ItemLike): Upgrade<PocketUpgradeSerialiser<*>> {
        return simpleWithCustomItem(XplatRegistries.POCKET_SERIALIZERS.getKey(serialiser), serialiser, item.asItem())
    }

    fun <V : IPocketUpgrade> simpleWithCustomItem(serialiser: Supplier<PocketUpgradeSerialiser<V>>, item: ItemLike): Upgrade<PocketUpgradeSerialiser<*>> {
        return simpleWithCustomItem(serialiser.get(), item)
    }

    fun <V : IPocketUpgrade, S : ItemLike> simpleWithCustomItem(serialiser: Supplier<PocketUpgradeSerialiser<V>>, item: Supplier<S>): Upgrade<PocketUpgradeSerialiser<*>> {
        return simpleWithCustomItem(serialiser.get(), item.get())
    }

    abstract fun registerUpgrades(addUpgrade: Consumer<Upgrade<PocketUpgradeSerialiser<*>>>)
}
