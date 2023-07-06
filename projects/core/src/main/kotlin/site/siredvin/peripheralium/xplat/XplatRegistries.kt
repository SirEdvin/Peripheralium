package site.siredvin.peripheralium.xplat

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.core.registries.Registries

object XplatRegistries {
    val ITEMS by lazy { PeripheraliumPlatform.wrap(Registries.ITEM) }
    val BLOCKS by lazy { PeripheraliumPlatform.wrap(Registries.BLOCK) }
    val FLUIDS by lazy { PeripheraliumPlatform.wrap(Registries.FLUID) }
    val ENTITY_TYPES by lazy { PeripheraliumPlatform.wrap(Registries.ENTITY_TYPE) }
    val TURTLE_SERIALIZERS by lazy { PeripheraliumPlatform.wrap(TurtleUpgradeSerialiser.registryId()) }
    val POCKET_SERIALIZERS by lazy { PeripheraliumPlatform.wrap(PocketUpgradeSerialiser.registryId()) }
    val RECIPE_TYPES by lazy { PeripheraliumPlatform.wrap(Registries.RECIPE_TYPE) }
}
