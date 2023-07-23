package site.siredvin.peripheralium.fabric

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.peripheralium.xplat.BaseInnerPlatform
import site.siredvin.peripheralium.xplat.MenuBuilder
import java.util.function.Supplier

abstract class FabricBaseInnerPlatform : BaseInnerPlatform {
    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = Registry.register(BuiltInRegistries.ITEM, key, item.get())
        return Supplier { registeredItem }
    }

    override fun <T : Block> registerBlock(
        key: ResourceLocation,
        block: Supplier<T>,
        itemFactory: (T) -> Item,
    ): Supplier<T> {
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, key, block.get())
        Registry.register(BuiltInRegistries.ITEM, key, itemFactory(registeredBlock))
        return Supplier { registeredBlock }
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        val registeredBlockEntityType = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntityTypeSup.get())
        return Supplier { registeredBlockEntityType }
    }

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        val menuType = ExtendedScreenHandlerType(builder::build)
        val registeredMenu = Registry.register(BuiltInRegistries.MENU, key, menuType)
        return Supplier { registeredMenu }
    }

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        val registeredTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab)
        return Supplier { registeredTab }
    }

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        @Suppress("UNCHECKED_CAST") val registry: Registry<TurtleUpgradeSerialiser<*>> = (
            BuiltInRegistries.REGISTRY.get(TurtleUpgradeSerialiser.registryId().location())
                ?: throw IllegalStateException("Something is not correct with turtle registry")
            ) as Registry<TurtleUpgradeSerialiser<*>>
        val registered = Registry.register(registry, key, serializer)
        return Supplier { registered }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        @Suppress("UNCHECKED_CAST") val registry: Registry<PocketUpgradeSerialiser<*>> = (
            BuiltInRegistries.REGISTRY.get(PocketUpgradeSerialiser.registryId().location())
                ?: throw IllegalStateException("Something is not correct with turtle registry")
            ) as Registry<PocketUpgradeSerialiser<*>>
        val registered = Registry.register(registry, key, serializer)
        return Supplier { registered }
    }

    override fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter): Supplier<Stat<ResourceLocation>> {
        val registeredStat = Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id)
        return Supplier { Stats.CUSTOM.get(registeredStat, formatter) }
    }
}
