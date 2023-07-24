package site.siredvin.peripheralium.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
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
import java.util.function.Supplier

interface BaseInnerPlatform {
    val modID: String

    fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T>

    fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T>

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T>

    fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>>

    fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab>

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>>
    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>>

    fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter = StatFormatter.DEFAULT): Supplier<Stat<ResourceLocation>>

    fun <C : Container, T : Recipe<C>> registerRecipeSerializer(key: ResourceLocation, serializer: RecipeSerializer<T>): Supplier<RecipeSerializer<T>>

    fun <V : Entity, T : EntityType<V>> registerEntity(key: ResourceLocation, entityTypeSup: Supplier<T>): Supplier<T>
}
