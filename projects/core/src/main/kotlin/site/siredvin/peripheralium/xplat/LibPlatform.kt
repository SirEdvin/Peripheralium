package site.siredvin.peripheralium.xplat

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import site.siredvin.peripheralium.data.language.ModInformationHolder
import java.util.function.Supplier

interface LibPlatform : ModInformationHolder {
    companion object {
        private var _IMPL: LibPlatform? = null
        private val registeredBlocks: MutableList<Supplier<out Block>> = mutableListOf()
        private val registeredItems: MutableList<Supplier<out Item>> = mutableListOf()

        val holder: ModInformationHolder
            get() = get()

        fun configure(impl: LibPlatform) {
            _IMPL = impl
        }

        private fun get(): LibPlatform {
            if (_IMPL == null) {
                throw IllegalStateException("You should init Peripheral Platform first")
            }
            return _IMPL!!
        }

        fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
            return get().registerItem(key, item)
        }

        fun <T : Item> registerItem(name: String, item: Supplier<T>): Supplier<T> {
            val registeredItem = registerItem(ResourceLocation(PeripheraliumCore.MOD_ID, name), item)
            registeredItems.add(registeredItem)
            return registeredItem
        }

        fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T> {
            return get().registerBlock(key, block, itemFactory)
        }

        fun <T : Block> registerBlock(name: String, block: Supplier<T>, itemFactory: (T) -> (Item) = { block -> DescriptiveBlockItem(block, Item.Properties()) }): Supplier<T> {
            val registeredBlock = get()
                .registerBlock(ResourceLocation(PeripheraliumCore.MOD_ID, name), block, itemFactory)
            registeredBlocks.add(registeredBlock)
            return registeredBlock
        }
    }

    fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T>
    fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T>

    override fun getBlocks(): List<Supplier<out Block>> {
        return registeredBlocks
    }

    override fun getItems(): List<Supplier<out Item>> {
        return registeredItems
    }
}
