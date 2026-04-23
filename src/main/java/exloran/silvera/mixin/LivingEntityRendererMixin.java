package com.silvera.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.silvera.module.api.ModuleManager;
import com.silvera.module.impl.cosmetic.HitColorModule;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @ModifyReturnValue(method = "getOverlay", at = @At("RETURN"))
    private int silvera_hitColor(int original, LivingEntity entity, float whiteOverlayProgress) {

        HitColorModule mod = ModuleManager.get(HitColorModule.class);

        if (mod != null && mod.isEnabled() && whiteOverlayProgress > 0) {
            return OverlayTexture.packUv(
                    OverlayTexture.getU(whiteOverlayProgress),
                    OverlayTexture.DEFAULT_UV
            );
        }

        return original;
    }
}
