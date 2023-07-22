package site.siredvin.peripheralium.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import site.siredvin.peripheralium.data.language.ModInformationHolder
import java.util.function.Supplier

interface BasePlatform {
    val baseInnerPlatform: BaseInnerPlatform
    val modInformationTracker: ModInformationTracker

    val holder: ModInformationHolder
        get() = modInformationTracker

    fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = baseInnerPlatform.registerItem(key, item)
        modInformationTracker.ITEMS.add(registeredItem)
        return registeredItem
    }

    fun <T : Item> registerItem(name: String, item: Supplier<T>): Supplier<T> {
        return registerItem(ResourceLocation(baseInnerPlatform.modID, name), item)
    }

    fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T> {
        return baseInnerPlatform.registerBlock(key, block, itemFactory)
    }

    fun <T : Block> registerBlock(name: String, block: Supplier<T>, itemFactory: (T) -> (Item) = { block -> DescriptiveBlockItem(block, Item.Properties()) }): Supplier<T> {
        val registeredBlock = baseInnerPlatform
            .registerBlock(ResourceLocation(baseInnerPlatform.modID, name), block, itemFactory)
        modInformationTracker.BLOCKS.add(registeredBlock)
        return registeredBlock
    }

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        name: String,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        return registerBlockEntity(ResourceLocation(baseInnerPlatform.modID, name), blockEntityTypeSup)
    }

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        return baseInnerPlatform.registerBlockEntity(key, blockEntityTypeSup)
    }

    fun <M : AbstractContainerMenu> registerMenu(
        name: String,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        return baseInnerPlatform.registerMenu(ResourceLocation(baseInnerPlatform.modID, name), builder)
    }

    fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        return baseInnerPlatform.registerCreativeTab(key, tab)
    }

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        name: String,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        return registerTurtleUpgrade(ResourceLocation(baseInnerPlatform.modID, name), serializer)
    }

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        val registered = baseInnerPlatform.registerTurtleUpgrade(key, serializer)
        modInformationTracker.TURTLE_UPGRADES.add(registered as Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>)
        return registered
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        name: String,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        return registerPocketUpgrade(ResourceLocation(baseInnerPlatform.modID, name), serializer)
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        val registered = baseInnerPlatform.registerPocketUpgrade(key, serializer)
        modInformationTracker.POCKET_UPGRADES.add(registered as Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>)
        return registered
    }

    fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter = StatFormatter.DEFAULT): Supplier<Stat<ResourceLocation>> {
        val registered = baseInnerPlatform.registerCustomStat(id, formatter)
        modInformationTracker.CUSTOM_STATS.add(registered)
        return registered
    }
}
