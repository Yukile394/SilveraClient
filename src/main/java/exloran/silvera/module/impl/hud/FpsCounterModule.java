package com.silvera.module.impl.hud;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * FPS Counter — anlık FPS + renk kodu (yeşil/sarı/kırmızı).
 */
public class FpsCounterModule extends Module {

    public FpsCounterModule() {
        super("FPS Counter", "Anlık FPS'i renk kodlu gösterir.", Category.HUD);
    }

    public void render(DrawContext ctx, int screenW, int screenH) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int fps = mc.getCurrentFps();

        String color;
        if (fps >= 60)      color = "§a"; // yeşil
        else if (fps >= 30) color = "§e"; // sarı
        else                color = "§c"; // kırmızı

        String text = color + fps + " §7FPS";
        int textW = mc.textRenderer.getWidth(text);
        ctx.drawTextWithShadow(mc.textRenderer, text, screenW - textW - 4, 4, 0xFFFFFF);
    }
}
