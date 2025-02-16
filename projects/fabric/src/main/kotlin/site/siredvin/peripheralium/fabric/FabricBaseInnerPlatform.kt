package site.siredvin.peripheralium.fabric

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats
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
import site.siredvin.peripheralium.xplat.BaseInnerPlatform
import site.siredvin.peripheralium.xplat.CreativeTabProvider
import site.siredvin.peripheralium.xplat.MenuBuilder
import java.util.function.Supplier

abstract class FabricBaseInnerPlatform : BaseInnerPlatform {
    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = Registry.register(Registry.ITEM, key, item.get())
        return Supplier { registeredItem }
    }

    override fun <T : Block> registerBlock(
        key: ResourceLocation,
        block: Supplier<T>,
        itemFactory: (T) -> Item,
    ): Supplier<T> {
        val registeredBlock = Registry.register(Registry.BLOCK, key, block.get())
        Registry.register(Registry.ITEM, key, itemFactory(registeredBlock))
        return Supplier { registeredBlock }
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        val registeredBlockEntityType = Registry.register(Registry.BLOCK_ENTITY_TYPE, key, blockEntityTypeSup.get())
        return Supplier { registeredBlockEntityType }
    }

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        val menuType = ExtendedScreenHandlerType(builder::build)
        val registeredMenu = Registry.register(Registry.MENU, key, menuType)
        return Supplier { registeredMenu }
    }

    override fun buildCreativeTab(key: ResourceLocation, tabProvider: CreativeTabProvider): Supplier<CreativeModeTab> {
        val tab = FabricItemGroupBuilder.create(key).icon(tabProvider::makeIcon).appendItems(tabProvider::appendItems).build()
        return Supplier { tab }
    }

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V> {
        ComputerCraftAPI.registerTurtleUpgrade(upgrade)
        return Supplier { upgrade }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V> {
        ComputerCraftAPI.registerPocketUpgrade(upgrade)
        return Supplier { upgrade }
    }

    override fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter): Supplier<Stat<ResourceLocation>> {
        val registeredStat = Registry.register(Registry.CUSTOM_STAT, id, id)
        return Supplier { Stats.CUSTOM.get(registeredStat, formatter) }
    }

    override fun <C : Container, T : Recipe<C>> registerRecipeSerializer(
        key: ResourceLocation,
        serializer: RecipeSerializer<T>,
    ): Supplier<RecipeSerializer<T>> {
        val registeredRecipe = Registry.register(Registry.RECIPE_SERIALIZER, key, serializer)
        return Supplier { registeredRecipe }
    }

    override fun <V : Entity, T : EntityType<V>> registerEntity(
        key: ResourceLocation,
        entityTypeSup: Supplier<T>,
    ): Supplier<T> {
        val registeredEntityType = Registry.register(Registry.ENTITY_TYPE, key, entityTypeSup.get())
        return Supplier { registeredEntityType }
    }
}
