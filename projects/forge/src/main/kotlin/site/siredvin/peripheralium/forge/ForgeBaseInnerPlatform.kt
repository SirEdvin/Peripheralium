package site.siredvin.peripheralium.forge

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats
import net.minecraft.world.Container
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.peripheralium.xplat.BaseInnerPlatform
import site.siredvin.peripheralium.xplat.MenuBuilder
import java.util.function.Supplier

abstract class ForgeBaseInnerPlatform : BaseInnerPlatform {
    open val blocksRegistry: DeferredRegister<Block>?
        get() = null
    open val itemsRegistry: DeferredRegister<Item>?
        get() = null
    open val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>> ?
        get() = null
    open val creativeTabRegistry: DeferredRegister<CreativeModeTab>?
        get() = null
    open val turtleSerializers: DeferredRegister<TurtleUpgradeSerialiser<*>>?
        get() = null
    open val pocketSerializers: DeferredRegister<PocketUpgradeSerialiser<*>>?
        get() = null
    open val menuTypes: DeferredRegister<MenuType<*>>?
        get() = null
    open val customStats: DeferredRegister<ResourceLocation>?
        get() = null

    open val recipeSerializers: DeferredRegister<RecipeSerializer<*>>?
        get() = null

    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        return itemsRegistry!!.register(key.path, item)
    }

    override fun <T : Block> registerBlock(
        key: ResourceLocation,
        block: Supplier<T>,
        itemFactory: (T) -> Item,
    ): Supplier<T> {
        val blockRegister = blocksRegistry!!.register(key.path, block)
        itemsRegistry!!.register(key.path) { itemFactory(blockRegister.get()) }
        return blockRegister
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        return blockEntityTypesRegistry!!.register(key.path, blockEntityTypeSup)
    }

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        val result = menuTypes!!.register(key.path) {
            IForgeMenuType.create(builder::build)
        }
        return result
    }

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        return creativeTabRegistry!!.register(key.path) { tab }
    }

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        return turtleSerializers!!.register(key.path) { serializer }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        return pocketSerializers!!.register(key.path) { serializer }
    }

    override fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter): Supplier<Stat<ResourceLocation>> {
        val registeredStat = customStats!!.register(id.path) { id }
        return Supplier { Stats.CUSTOM.get(registeredStat.get(), formatter) }
    }

    override fun <C : Container, T : Recipe<C>> registerRecipeSerializer(
        key: ResourceLocation,
        serializer: RecipeSerializer<T>,
    ): Supplier<RecipeSerializer<T>> {
        return recipeSerializers!!.register(key.path) { serializer }
    }
}
