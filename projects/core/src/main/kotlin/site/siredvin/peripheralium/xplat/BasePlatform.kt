package site.siredvin.peripheralium.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
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
        modInformationTracker.internalItems.add(registeredItem)
        return registeredItem
    }

    fun <T : Item> registerItem(name: String, item: Supplier<T>): Supplier<T> = registerItem(ResourceLocation(baseInnerPlatform.modID, name), item)

    fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T> = baseInnerPlatform.registerBlock(key, block, itemFactory)

    fun <T : Block> registerBlock(name: String, block: Supplier<T>, itemFactory: (T) -> (Item) = { DescriptiveBlockItem(it, Item.Properties()) }): Supplier<T> {
        val registeredBlock = baseInnerPlatform
            .registerBlock(ResourceLocation(baseInnerPlatform.modID, name), block, itemFactory)
        modInformationTracker.internalBlocks.add(registeredBlock)
        return registeredBlock
    }

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        name: String,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> = registerBlockEntity(ResourceLocation(baseInnerPlatform.modID, name), blockEntityTypeSup)

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> = baseInnerPlatform.registerBlockEntity(key, blockEntityTypeSup)

    fun <M : AbstractContainerMenu> registerMenu(
        name: String,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> = baseInnerPlatform.registerMenu(ResourceLocation(baseInnerPlatform.modID, name), builder)

    fun buildCreativeTab(key: ResourceLocation, tabProvider: CreativeTabProvider): Supplier<CreativeModeTab> = baseInnerPlatform.buildCreativeTab(key, tabProvider)

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        name: String,
        upgrade: V,
    ): Supplier<V> = registerTurtleUpgrade(ResourceLocation(baseInnerPlatform.modID, name), upgrade)

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V> {
        val registered = baseInnerPlatform.registerTurtleUpgrade(key, upgrade)
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalTurtleUpgrades.add(registered as Supplier<out ITurtleUpgrade>)
        return registered
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        name: String,
        upgrade: V,
    ): Supplier<V> = registerPocketUpgrade(ResourceLocation(baseInnerPlatform.modID, name), upgrade)

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V> {
        val registered = baseInnerPlatform.registerPocketUpgrade(key, upgrade)
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalPocketUpgrades.add(registered as Supplier<out IPocketUpgrade>)
        return registered
    }

    fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter = StatFormatter.DEFAULT): Supplier<Stat<ResourceLocation>> {
        val registered = baseInnerPlatform.registerCustomStat(id, formatter)
        modInformationTracker.internalCustomStats.add(registered)
        return registered
    }

    fun <C : Container, T : Recipe<C>> registerRecipeSerializer(key: ResourceLocation, serializer: RecipeSerializer<T>): Supplier<RecipeSerializer<T>> = baseInnerPlatform.registerRecipeSerializer(key, serializer)

    fun <V : Entity, T : EntityType<V>> registerEntity(key: ResourceLocation, entityTypeSup: Supplier<T>): Supplier<T> = baseInnerPlatform.registerEntity(key, entityTypeSup)
}
