package com.silvera.render;

import com.silvera.module.api.ModuleManager;
import com.silvera.module.impl.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * HudRenderer — tüm HUD modüllerini tek bir noktadan çizer.
 * GameRendererMixin'den her frame çağrılır.
 */
public class HudRenderer {

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (mc.options.hudHidden) return;

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();

        CoordinatesModule coords = ModuleManager.get(CoordinatesModule.class);
        if (coords != null && coords.isEnabled()) coords.render(ctx, sw, sh);

        FpsCounterModule fps = ModuleManager.get(FpsCounterModule.class);
        if (fps != null && fps.isEnabled()) fps.render(ctx, sw, sh);

        BiomeDisplayModule biome = ModuleManager.get(BiomeDisplayModule.class);
        if (biome != null && biome.isEnabled()) biome.render(ctx, sw, sh);

        DirectionHudModule dir = ModuleManager.get(DirectionHudModule.class);
        if (dir != null && dir.isEnabled()) dir.render(ctx, sw, sh);

        ArmorHudModule armor = ModuleManager.get(ArmorHudModule.class);
        if (armor != null && armor.isEnabled()) armor.render(ctx, sw, sh);

        PotionHudModule potion = ModuleManager.get(PotionHudModule.class);
        if (potion != null && potion.isEnabled()) potion.render(ctx, sw, sh);

        // Silvera watermark
        renderWatermark(ctx, sw);
    }

    private static void renderWatermark(DrawContext ctx, int sw) {
        MinecraftClient mc = MinecraftClient.getInstance();
        // Gradient-style text watermark
        String mark = "§dSilvera §7Client §8v1.0";
        int tw = mc.textRenderer.getWidth(mark);
        // Arka plan kutusu
        ctx.fill(sw - tw - 8, 0, sw, 13, 0x88000000);
        ctx.drawTextWithShadow(mc.textRenderer, mark, sw - tw - 4, 2, 0xFFFFFF);
    }
}
