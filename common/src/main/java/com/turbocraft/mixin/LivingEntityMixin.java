package com.turbocraft.mixin;

import com.turbocraft.TurboConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Far-mob collision throttling: pushing between crowded mobs (mob farms,
 * villager halls) is one of the most expensive things the server does.
 * Mobs far from every player resolve collisions only every N ticks.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "pushEntities", at = @At("HEAD"), cancellable = true, require = 0)
    private void turbocraft$throttleFarPushing(CallbackInfo ci) {
        TurboConfig cfg = TurboConfig.get();
        if (cfg.farMobDistance <= 0 || cfg.farMobAiInterval <= 1) return;

        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Mob)) return;            // players always collide normally
        if (self.level().isClientSide()) return;
        if ((self.tickCount + self.getId()) % cfg.farMobAiInterval == 0) return;

        if (self.level().getNearestPlayer(self, cfg.farMobDistance) == null) {
            ci.cancel();
        }
    }
}
