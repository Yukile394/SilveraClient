package com.silvera.mixin;

import com.silvera.module.impl.cosmetic.ZoomModule;
import com.silvera.render.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyReturnValue;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * GameRendererMixin
 *  - FOV modifikasyonu (Zoom)
 *  - HUD render hook
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /** Zoom: getFov'un dönüş değerini yakala ve modifiye et */
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float silvera_modifyFov(float original) {
        return ZoomModule.getModifiedFov(original);
    }
}
