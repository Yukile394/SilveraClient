package com.silvera.mixin;

import com.silvera.module.api.ModuleManager;
import com.silvera.module.impl.particle.HitParticleModule;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * WorldRendererMixin — bir entity'e vurulduğunda hit particle tetikler.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "processWorldEvent", at = @At("HEAD"))
    private void silvera_onWorldEvent(int eventId, net.minecraft.util.math.BlockPos pos,
                                       int data, CallbackInfo ci) {
        // 2006 = sweep attack particles
        if (eventId == 2006) {
            HitParticleModule mod = ModuleManager.get(HitParticleModule.class);
            if (mod != null && mod.isEnabled()) {
                HitParticleModule.spawnHitEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }
        }
    }
}
