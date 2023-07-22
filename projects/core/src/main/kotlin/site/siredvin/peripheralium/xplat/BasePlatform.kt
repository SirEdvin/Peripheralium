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

interface BasePlatform : ModInformationHolder {
    companion object {
        private val ITEMS: MutableList<Supplier<out Item>> = mutableListOf()
        private val BLOCKS: MutableList<Supplier<out Block>> = mutableListOf()
        private val POCKET_UPGRADES: MutableList<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>> = mutableListOf()
        private val TURTLE_UPGRADES: MutableList<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>> = mutableListOf()
        private val CUSTOM_STATS: MutableList<Supplier<ResourceLocation>> = mutableListOf()
    }

    val baseInnerPlatform: BaseInnerPlatform

    val holder: ModInformationHolder
        get() = this

    fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = baseInnerPlatform.registerItem(key, item)
        ITEMS.add(registeredItem)
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
        BLOCKS.add(registeredBlock)
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
        TURTLE_UPGRADES.add(registered as Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>)
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
        POCKET_UPGRADES.add(registered as Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>)
        return registered
    }

    fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter = StatFormatter.DEFAULT): Supplier<Stat<*>> {
        return baseInnerPlatform.registerCustomStat(id, formatter)
    }

    override val blocks: List<Supplier<out Block>>
        get() = BLOCKS

    override val items: List<Supplier<out Item>>
        get() = ITEMS

    override val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = TURTLE_UPGRADES

    override val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = POCKET_UPGRADES

    override val customStats: List<Supplier<ResourceLocation>>
        get() = CUSTOM_STATS
}
