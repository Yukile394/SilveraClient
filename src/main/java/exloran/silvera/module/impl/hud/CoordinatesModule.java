package com.silvera.module.impl.hud;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

/**
 * Coordinates HUD — XYZ koordinatlarını ekranda gösterir.
 */
public class CoordinatesModule extends Module {

    public CoordinatesModule() {
        super("Coordinates", "XYZ koordinatlarını ekranda gösterir.", Category.HUD);
    }

    public void render(DrawContext ctx, int screenW, int screenH) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        String text = String.format("§7XYZ §f%.1f §7/ §f%.1f §7/ §f%.1f", x, y, z);
        ctx.drawTextWithShadow(mc.textRenderer, text, 4, 4, 0xFFFFFF);

        BlockPos bp = mc.player.getBlockPos();
        String chunk = String.format("§7Chunk §f%d §7/ §f%d", bp.getX() >> 4, bp.getZ() >> 4);
        ctx.drawTextWithShadow(mc.textRenderer, chunk, 4, 14, 0xFFFFFF);
    }
}
