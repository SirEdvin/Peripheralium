package site.siredvin.peripheralium

import dan200.computercraft.shared.CommonHooks
import net.minecraft.world.entity.item.ItemEntity
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = PeripheraliumCore.MOD_ID)
object ForgeCommonHooks {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onEntitySpawn(event: EntityJoinLevelEvent) {
        if (CommonHooks.onEntitySpawn(event.entity)) event.isCanceled = true
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onLivingDrops(event: LivingDropsEvent) {
        event.drops.removeIf { itemEntity: ItemEntity ->
            CommonHooks.onLivingDrop(
                event.entity,
                itemEntity.item
            )
        }
    }
}