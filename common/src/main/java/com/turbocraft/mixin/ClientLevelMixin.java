package com.turbocraft.mixin;

import com.turbocraft.TurboConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Particle culling: particles spawned farther than particleDistance blocks
 * from the player are dropped before they are even created.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void turbocraft$cullFarParticles(ParticleOptions options, double x, double y, double z,
                                             double xd, double yd, double zd, CallbackInfo ci) {
        TurboConfig cfg = TurboConfig.get();
        if (cfg.particleDistance <= 0) return;
        Player player = Minecraft.getInstance().player;
        if (player != null && player.distanceToSqr(x, y, z) > cfg.particleDistanceSq) {
            ci.cancel();
        }
    }
}
