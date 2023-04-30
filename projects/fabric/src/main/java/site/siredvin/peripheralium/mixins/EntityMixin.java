package site.siredvin.peripheralium.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import site.siredvin.peripheralium.xplat.LibCommonHooks;

@Mixin(Entity.class)
class EntityMixin {
    @Inject(
            method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            cancellable = true
    )
    @SuppressWarnings("UnusedMethod")
    private void spawnAtLocation(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cb) {
        if (LibCommonHooks.INSTANCE.onLivingDrop((Entity) (Object) this, stack)) cb.setReturnValue(null);
    }
}
