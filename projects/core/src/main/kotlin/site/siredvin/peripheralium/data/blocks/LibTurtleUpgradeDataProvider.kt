package site.siredvin.peripheralium.data.blocks

import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.function.Consumer
import java.util.function.Supplier

abstract class LibTurtleUpgradeDataProvider(output: PackOutput, serializers: List<Supplier<TurtleUpgradeSerialiser<*>>>) : TurtleUpgradeDataProvider(output) {
    private val serializers: List<ResourceLocation> = serializers.map {
        XplatRegistries.TURTLE_SERIALIZERS.getKey(it.get())
    }
    override fun addUpgrades(addUpgrade: Consumer<Upgrade<TurtleUpgradeSerialiser<*>>>) {
        val touchedSerializers = mutableSetOf<ResourceLocation>()
        registerUpgrades {
            addUpgrade.accept(it)
            touchedSerializers.add(XplatRegistries.TURTLE_SERIALIZERS.getKey(it.serialiser))
        }
        val missedSerializers = serializers.filter { !touchedSerializers.contains(it) }
        if (missedSerializers.isNotEmpty()) {
            throw IllegalArgumentException("Some serializers don't have default items: $missedSerializers")
        }
    }

    fun <V : ITurtleUpgrade> simpleWithCustomItem(serialiser: TurtleUpgradeSerialiser<V>, item: ItemLike): Upgrade<TurtleUpgradeSerialiser<*>> {
        return simpleWithCustomItem(XplatRegistries.TURTLE_SERIALIZERS.getKey(serialiser), serialiser, item.asItem())
    }

    fun <V : ITurtleUpgrade> simpleWithCustomItem(serialiser: Supplier<TurtleUpgradeSerialiser<V>>, item: ItemLike): Upgrade<TurtleUpgradeSerialiser<*>> {
        return simpleWithCustomItem(serialiser.get(), item)
    }

    fun <V : ITurtleUpgrade, S : ItemLike> simpleWithCustomItem(serialiser: Supplier<TurtleUpgradeSerialiser<V>>, item: Supplier<S>): Upgrade<TurtleUpgradeSerialiser<*>> {
        return simpleWithCustomItem(serialiser.get(), item.get())
    }

    abstract fun registerUpgrades(addUpgrade: Consumer<Upgrade<TurtleUpgradeSerialiser<*>>>)
}
