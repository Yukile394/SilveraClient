package com.silvera.mixin;

import com.silvera.module.api.ModuleManager;
import com.silvera.module.impl.cosmetic.CapeAnimationModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * PlayerEntityRendererMixin — pelerin animasyonunu güçlendirir.
 */
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "renderCapeFeature", at = @At("HEAD"), remap = false, require = 0)
    private void silvera_capeAnimation(MatrixStack matrices,
                                        AbstractClientPlayerEntity player,
                                        float tickDelta, CallbackInfo ci) {
        CapeAnimationModule mod = ModuleManager.get(CapeAnimationModule.class);
        if (mod == null || !mod.isEnabled()) return;
        // Cape flutter artırma — matrix scale ile dramatik efekt
        matrices.scale(CapeAnimationModule.capeFlutterIntensity,
                CapeAnimationModule.capeFlutterIntensity, 1.0f);
    }
}
