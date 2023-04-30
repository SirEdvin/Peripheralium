package site.siredvin.peripheralium.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.data.blocks.LootTableHelper
import java.util.function.BiConsumer


object LibLootTableProvider {
    fun getTables(): List<LootTableProvider.SubProviderEntry> {
        return listOf(
            LootTableProvider.SubProviderEntry({ LootTableSubProvider {
                registerBlocks(it)
            } }, LootContextParamSets.BLOCK)
        )
    }

    fun registerBlocks(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
        LootTableHelper.dropSelf(consumer, Blocks.PERIPHERALIUM_BLOCK)
    }
}