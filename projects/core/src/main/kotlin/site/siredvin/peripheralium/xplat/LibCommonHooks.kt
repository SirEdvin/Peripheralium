package site.siredvin.peripheralium.xplat

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.util.world.DropConsumer

object LibCommonHooks {
    fun onEntitySpawn(entity: Entity): Boolean {
        return DropConsumer.onEntitySpawn(entity)
    }

    fun onLivingDrop(entity: Entity, stack: ItemStack?): Boolean {
        return DropConsumer.onLivingDrop(entity, stack!!)
    }

    fun onRegister() {
        Blocks.doSomething()
        Items.doSomething()
    }
}