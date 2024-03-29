package site.siredvin.peripheralium.data.blocks

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import site.siredvin.peripheralium.data.language.ModInformationHolder
import java.util.function.BiConsumer
import java.util.function.Supplier

class LootTableHelper(private val informationHolder: ModInformationHolder) {
    private val registeredBlocks: MutableSet<Block> = mutableSetOf()

    fun dropSelf(consumer: BiConsumer<ResourceLocation, LootTable.Builder>, wrapper: Supplier<out Block>) {
        dropBlock(consumer, wrapper, LootItem.lootTableItem(wrapper.get()), ExplosionCondition.survivesExplosion())
    }

    fun dropNamedBlock(consumer: BiConsumer<ResourceLocation, LootTable.Builder>, wrapper: Supplier<out Block>) {
        dropBlock(
            consumer,
            wrapper,
            LootItem.lootTableItem(wrapper.get()).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)),
            ExplosionCondition.survivesExplosion(),
        )
    }

    fun dropBlock(
        consumer: BiConsumer<ResourceLocation, LootTable.Builder>,
        wrapper: Supplier<out Block>,
        drop: LootPoolEntryContainer.Builder<*>,
        condition: LootItemCondition.Builder,
    ) {
        val block = wrapper.get()
        consumer.accept(
            block.lootTable,
            LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1f)).add(drop).`when`(condition),
            ),
        )
        registeredBlocks.add(block)
    }

    /**
     * Method that used to notify validation, that block will take care of drop logic by himself
     */
    fun computedDrop(wrapper: Supplier<out Block>) {
        registeredBlocks.add(wrapper.get())
    }

    fun validate() {
        informationHolder.blocks.forEach {
            if (!registeredBlocks.contains(it.get())) {
                throw Exception("${it.get().descriptionId} doesn't have registered drop")
            }
        }
    }
}
