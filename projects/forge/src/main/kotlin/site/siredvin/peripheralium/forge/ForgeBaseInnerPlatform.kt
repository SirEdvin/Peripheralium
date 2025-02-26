package site.siredvin.peripheralium.forge

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
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
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.peripheralium.xplat.BaseInnerPlatform
import site.siredvin.peripheralium.xplat.CreativeTabProvider
import site.siredvin.peripheralium.xplat.MenuBuilder
import java.util.function.Supplier

abstract class ForgeBaseInnerPlatform : BaseInnerPlatform {
    open val blocksRegistry: DeferredRegister<Block>?
        get() = null
    open val itemsRegistry: DeferredRegister<Item>?
        get() = null
    open val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>?
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

    open val entityTypesRegistry: DeferredRegister<EntityType<*>>?
        get() = null

    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> = itemsRegistry!!.register(key.path, item)

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
    ): Supplier<T> = blockEntityTypesRegistry!!.register(key.path, blockEntityTypeSup)

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        val result = menuTypes!!.register(key.path) {
            IForgeMenuType.create(builder::build)
        }
        return result
    }

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(key: ResourceLocation, upgrade: V): Supplier<V> {
        turtleSerializers!!.register(key.path) { TurtleUpgradeSerialiser.simple<V> { upgrade } }
        return Supplier { upgrade }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(key: ResourceLocation, upgrade: V): Supplier<V> {
        pocketSerializers!!.register(key.path) { PocketUpgradeSerialiser.simple<V> { upgrade } }
        return Supplier { upgrade }
    }

    override fun buildCreativeTab(key: ResourceLocation, tabProvider: CreativeTabProvider): Supplier<CreativeModeTab> {
        TODO("Not yet implemented")
    }

    override fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter): Supplier<Stat<ResourceLocation>> {
        val registeredStat = customStats!!.register(id.path) { id }
        return Supplier { Stats.CUSTOM.get(registeredStat.get(), formatter) }
    }

    override fun <C : Container, T : Recipe<C>> registerRecipeSerializer(
        key: ResourceLocation,
        serializer: RecipeSerializer<T>,
    ): Supplier<RecipeSerializer<T>> = recipeSerializers!!.register(key.path) { serializer }

    override fun <V : Entity, T : EntityType<V>> registerEntity(
        key: ResourceLocation,
        entityTypeSup: Supplier<T>,
    ): Supplier<T> = entityTypesRegistry!!.register(key.path, entityTypeSup)
}
