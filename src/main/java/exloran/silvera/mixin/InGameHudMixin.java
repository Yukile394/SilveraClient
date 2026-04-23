package com.silvera.mixin;

import com.silvera.render.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * InGameHudMixin — Silvera HUD'u vanilla HUD'un üstüne çizer.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void silvera_renderHud(DrawContext ctx, float tickDelta, CallbackInfo ci) {
        HudRenderer.render(ctx);
    }
}
