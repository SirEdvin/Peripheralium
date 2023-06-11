package site.siredvin.peripheralium.data.blocks

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
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

    abstract fun registerUpgrades(addUpgrade: Consumer<Upgrade<PocketUpgradeSerialiser<*>>>)
}
