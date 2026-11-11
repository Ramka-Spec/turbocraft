package com.turbocraft.mixin;

import com.turbocraft.TurboConfig;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Far-mob AI throttling: mobs that are far from every player run their
 * goal selection / pathfinding only every N ticks instead of every tick.
 * Movement physics still runs every tick, so mobs don't freeze in mid-air.
 */
@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true, require = 0)
    private void turbocraft$throttleFarAi(CallbackInfo ci) {
        TurboConfig cfg = TurboConfig.get();
        if (cfg.farMobDistance <= 0 || cfg.farMobAiInterval <= 1) return;

        Mob self = (Mob) (Object) this;
        // Stagger mobs by id so they don't all think on the same tick
        if ((self.tickCount + self.getId()) % cfg.farMobAiInterval == 0) return;
        if (self.getTarget() != null) return;          // keep fights responsive
        if (self.isPassenger() || self.isVehicle()) return;

        if (self.level().getNearestPlayer(self, cfg.farMobDistance) == null) {
            ci.cancel();
        }
    }
}
