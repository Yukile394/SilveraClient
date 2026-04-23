package com.silvera.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.silvera.module.impl.cosmetic.ZoomModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float silvera_modifyFov(float original) {
        return (float) ZoomModule.getModifiedFov(original);
    }
}
