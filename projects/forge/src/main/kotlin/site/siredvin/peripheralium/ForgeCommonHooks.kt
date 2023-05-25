package site.siredvin.peripheralium

import net.minecraft.world.entity.item.ItemEntity
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.peripheralium.xplat.LibCommonHooks

@Mod.EventBusSubscriber(modid = PeripheraliumCore.MOD_ID)
object ForgeCommonHooks {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onEntitySpawn(event: EntityJoinLevelEvent) {
        if (LibCommonHooks.onEntitySpawn(event.entity)) event.isCanceled = true
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onLivingDrops(event: LivingDropsEvent) {
        event.drops.removeIf { itemEntity: ItemEntity ->
            LibCommonHooks.onLivingDrop(
                event.entity,
                itemEntity.item,
            )
        }
    }
}
