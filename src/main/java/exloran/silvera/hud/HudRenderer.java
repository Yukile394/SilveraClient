package com.silvera.client.hud;

import com.silvera.client.config.SilveraConfig;
import com.silvera.client.key.Keybinds;
import com.silvera.client.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

public class HudRenderer {

    private static int cps = 0;
    private static int clickBuffer = 0;
    private static long lastCpsTick = 0;

    public static void onLeftClick() { clickBuffer++; }

    private static void handleKeybinds() {
        if (Keybinds.toggleHud.wasPressed())
            SilveraConfig.hudEnabled = !SilveraConfig.hudEnabled;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            handleKeybinds();
            updateCps();

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) return;

            ParticleHud.tick();
            ParticleHud.render(drawContext);

            if (!SilveraConfig.hudEnabled) return;

            renderWatermark(drawContext, mc);
            renderArrayList(drawContext, mc);
            renderKeystrokes(drawContext, mc);
            renderStatsBar(drawContext, mc);
        });
    }

    private static void updateCps() {
        long now = System.currentTimeMillis();
        if (now - lastCpsTick >= 1000) {
            cps = clickBuffer;
            clickBuffer = 0;
            lastCpsTick = now;
        }
    }

    // ── WATERMARK ───────────────────────────────────────────────────────────
    private static void renderWatermark(DrawContext ctx, MinecraftClient mc) {
        String text = SilveraConfig.watermark;
        int x = 4, y = 4;
        int w = mc.textRenderer.getWidth(text) + 14;
        int h = 14;

        RenderUtil.drawRoundedRect(ctx, x, y, w, h, 3, 0xCC000000);
        RenderUtil.drawRoundedRect(ctx, x, y, 2, h, 1, 0xFFFF2020);

        ctx.drawText(mc.textRenderer, "§c" + text, x + 5, y + 3, 0xFFFFFF, true);
    }

    // ── ARRAYLIST ───────────────────────────────────────────────────────────
    private static void renderArrayList(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.arrayListEnabled) return;
        String[] modules = {"TrailFX", "HitFX", "Keystrokes", "CPS", "FPS"};
        int screenW = mc.getWindow().getScaledWidth();
        int y = 4;

        for (String mod : modules) {
            int textW = mc.textRenderer.getWidth(mod);
            int x = screenW - textW - 10;
            int w = textW + 8;

            RenderUtil.drawRoundedRect(ctx, x, y, w, 12, 3, 0xCC000000);
            RenderUtil.drawRoundedRect(ctx, x + w - 2, y, 2, 12, 1, 0xFFFF2020);

            ctx.drawText(mc.textRenderer, "§c" + mod, x + 3, y + 2, 0xFFFFFF, true);
            y += 13;
        }
    }

    // ── KEYSTROKES ──────────────────────────────────────────────────────────
    private static void renderKeystrokes(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.keystrokesEnabled) return;
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();
        int baseX = screenW - 60;
        int baseY = screenH - 72;

        boolean w     = mc.options.forwardKey.isPressed();
        boolean s     = mc.options.backKey.isPressed();
        boolean a     = mc.options.leftKey.isPressed();
        boolean d     = mc.options.rightKey.isPressed();
        boolean space = mc.options.jumpKey.isPressed();
        boolean lmb   = mc.options.attackKey.isPressed();
        boolean rmb   = mc.options.useKey.isPressed();

        drawSmoothKey(ctx, mc, "W",   baseX + 19, baseY,       14, 12, w);
        drawSmoothKey(ctx, mc, "A",   baseX,      baseY + 14,  14, 12, a);
        drawSmoothKey(ctx, mc, "S",   baseX + 19, baseY + 14,  14, 12, s);
        drawSmoothKey(ctx, mc, "D",   baseX + 38, baseY + 14,  14, 12, d);
        drawSmoothKey(ctx, mc, "▬",   baseX,      baseY + 28,  52, 10, space);
        drawSmoothKey(ctx, mc, "LMB", baseX,      baseY + 40,  24, 12, lmb);
        drawSmoothKey(ctx, mc, "RMB", baseX + 28, baseY + 40,  24, 12, rmb);

        if (SilveraConfig.cpsEnabled)
            ctx.drawText(mc.textRenderer,
                "§7" + cps + " §cCPS", baseX + 4, baseY + 55, 0xFFFFFF, true);
    }

    private static void drawSmoothKey(DrawContext ctx, MinecraftClient mc,
                                       String label, int x, int y,
                                       int w, int h, boolean pressed) {
        int border = pressed ? 0xFFFF4444 : 0xFF333333;
        int bg     = pressed ? 0xCCFF2020 : 0xAA111111;
        int text   = pressed ? 0xFFFFFFFF : 0xFF888888;

        // Border (1px dışta)
        RenderUtil.drawRoundedRect(ctx, x - 1, y - 1, w + 2, h + 2, 3, border);
        // Arka plan
        RenderUtil.drawRoundedRect(ctx, x, y, w, h, 3, bg);

        // Etiket ortala
        int tw = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label,
            x + (w - tw) / 2,
            y + (h - 7) / 2,
            text, false);
    }

    // ── STATS BAR ───────────────────────────────────────────────────────────
    private static void renderStatsBar(DrawContext ctx, MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        int screenH = mc.getWindow().getScaledHeight();
        int x = 4, y = screenH - 28;

        int hp    = (int) p.getHealth();
        int maxHp = (int) p.getMaxHealth();
        int fps   = mc.getCurrentFps();

        // Ana panel
        RenderUtil.drawRoundedRect(ctx, x, y, 104, 24, 3, 0xCC000000);
        // Sol kırmızı aksan
        RenderUtil.drawRoundedRect(ctx, x, y, 2, 24, 1, 0xFFFF2020);

        ctx.drawText(mc.textRenderer,
            "§c❤ §f" + hp + "§7/" + maxHp, x + 5, y + 3,  0xFFFFFF, true);

        if (SilveraConfig.fpsEnabled)
            ctx.drawText(mc.textRenderer,
                "§7FPS §a" + fps,           x + 5, y + 13, 0xFFFFFF, true);

        // HP bar
        float ratio = maxHp > 0 ? (float) hp / maxHp : 0;
        int barColor = ratio > 0.6f ? 0xFF44FF44
                     : ratio > 0.3f ? 0xFFFFAA00
                     :                0xFFFF2020;

        RenderUtil.drawRoundedRect(ctx, x + 4, y + 22, 96,                 4, 2, 0xFF222222);
        RenderUtil.drawRoundedRect(ctx, x + 4, y + 22, (int)(96 * ratio),  4, 2, barColor);
    }
}
