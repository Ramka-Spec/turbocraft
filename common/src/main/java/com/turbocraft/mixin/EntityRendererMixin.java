package com.turbocraft.mixin;

import com.turbocraft.TurboConfig;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Distance culling: skip drawing entities that are too far from the camera.
 * Cheap check that runs before vanilla's own (more expensive) visibility tests.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void turbocraft$distanceCull(Entity entity, Frustum frustum, double camX, double camY, double camZ,
                                         CallbackInfoReturnable<Boolean> cir) {
        TurboConfig cfg = TurboConfig.get();
        if (cfg.entityRenderDistance <= 0) return;
        if (entity instanceof Player) return;          // always see other players
        if (entity.getBbWidth() > 4.0F) return;        // big things (bosses) stay visible
        if (entity.isCurrentlyGlowing()) return;       // glowing = should be seen through walls

        if (entity.distanceToSqr(camX, camY, camZ) > cfg.entityRenderDistanceSq) {
            cir.setReturnValue(false);
        }
    }
}
