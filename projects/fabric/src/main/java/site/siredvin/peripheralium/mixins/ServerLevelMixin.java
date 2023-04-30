package site.siredvin.peripheralium.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import site.siredvin.peripheralium.xplat.LibCommonHooks;

@Mixin(ServerLevel.class)
class ServerLevelMixin {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings("UnusedMethod")
    private void addEntity(Entity entity, CallbackInfoReturnable<Boolean> cb) {
        if (LibCommonHooks.INSTANCE.onEntitySpawn(entity)) cb.setReturnValue(true);
    }
}