package com.silvera.mixin;

import com.silvera.module.api.ModuleManager;
import com.silvera.module.impl.cosmetic.HitColorModule;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyReturnValue;

/**
 * LivingEntityRendererMixin — vurulduğunda özel renk flash.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @ModifyReturnValue(method = "getOverlay", at = @At("RETURN"))
    private int silvera_hitColor(int original, LivingEntity entity, float whiteOverlayProgress) {
        HitColorModule mod = ModuleManager.get(HitColorModule.class);
        if (mod != null && mod.isEnabled() && whiteOverlayProgress > 0) {
            // Beyaz flash yerine cyan flash
            return net.minecraft.client.render.OverlayTexture.packUv(
                    net.minecraft.client.render.OverlayTexture.getU(whiteOverlayProgress),
                    net.minecraft.client.render.OverlayTexture.WHITE_OVERLAY_V
            );
        }
        return original;
    }
}
