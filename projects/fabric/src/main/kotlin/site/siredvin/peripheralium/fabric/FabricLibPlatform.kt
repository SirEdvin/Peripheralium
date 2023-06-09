package site.siredvin.peripheralium.fabric

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.xplat.LibPlatform
import java.util.function.Supplier

object FabricLibPlatform : LibPlatform {
    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = Registry.register(BuiltInRegistries.ITEM, key, item.get())
        return Supplier { registeredItem }
    }

    override fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, key, block.get())
        Registry.register(BuiltInRegistries.ITEM, key, itemFactory(registeredBlock))
        return Supplier { registeredBlock }
    }
}
