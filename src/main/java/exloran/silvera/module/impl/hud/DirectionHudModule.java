package com.silvera.module.impl.hud;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Direction HUD — baktığın yönü (N/S/E/W) ve yaw açısını gösterir.
 */
public class DirectionHudModule extends Module {

    public DirectionHudModule() {
        super("Direction HUD", "Baktığın yönü gösterir.", Category.HUD);
    }

    public void render(DrawContext ctx, int screenW, int screenH) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        float yaw = mc.player.getYaw();
        // Normalize 0-360
        yaw = ((yaw % 360) + 360) % 360;

        String dir;
        if      (yaw < 22.5  || yaw >= 337.5) dir = "§cS";
        else if (yaw < 67.5)                  dir = "§eSW";
        else if (yaw < 112.5)                 dir = "§aW";
        else if (yaw < 157.5)                 dir = "§eNW";
        else if (yaw < 202.5)                 dir = "§bN";
        else if (yaw < 247.5)                 dir = "§eNE";
        else if (yaw < 292.5)                 dir = "§aE";
        else                                  dir = "§eSE";

        String text = dir + " §7(" + (int) yaw + "°)";
        int textW = mc.textRenderer.getWidth(text);
        ctx.drawTextWithShadow(mc.textRenderer, text, screenW - textW - 4, 24, 0xFFFFFF);
    }
}
