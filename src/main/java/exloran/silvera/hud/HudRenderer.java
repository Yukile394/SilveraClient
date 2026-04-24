package com.silvera.client.hud;

import com.silvera.client.config.SilveraConfig;
import com.silvera.client.key.Keybinds;
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
        if (Keybinds.toggleHud.wasPressed()) {
            SilveraConfig.hudEnabled = !SilveraConfig.hudEnabled;
        }
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            handleKeybinds();
            updateCps();

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) return;

            // Particle HUD (2D ekran partikülleri — kalpler silindi)
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

    private static void renderWatermark(DrawContext ctx, MinecraftClient mc) {
        String text = SilveraConfig.watermark;
        int x = 4, y = 4;
        int w = mc.textRenderer.getWidth(text) + 10;
        ctx.fill(x - 2, y - 2, x + w, y + 12, 0xAA000000);
        ctx.fill(x - 2, y - 2, x - 1, y + 12, 0xFFFF2020);
        ctx.drawText(mc.textRenderer, "§c" + text, x + 3, y + 2, 0xFFFFFF, true);
    }

    private static void renderArrayList(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.arrayListEnabled) return;
        String[] modules = {"TrailFX", "HitFX", "Keystrokes", "CPS", "FPS"};
        int screenW = mc.getWindow().getScaledWidth();
        int y = 4;
        for (String mod : modules) {
            int textW = mc.textRenderer.getWidth(mod);
            int x = screenW - textW - 6;
            ctx.fill(x - 2, y - 1, screenW - 2, y + 9, 0xAA000000);
            ctx.fill(screenW - 3, y - 1, screenW - 2, y + 9, 0xFFFF2020);
            ctx.drawText(mc.textRenderer, "§c" + mod, x, y + 1, 0xFFFFFF, true);
            y += 12;
        }
    }

    private static void renderKeystrokes(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.keystrokesEnabled) return;
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();
        int baseX = screenW - 58;
        int baseY = screenH - 68;

        boolean w     = mc.options.forwardKey.isPressed();
        boolean s     = mc.options.backKey.isPressed();
        boolean a     = mc.options.leftKey.isPressed();
        boolean d     = mc.options.rightKey.isPressed();
        boolean space = mc.options.jumpKey.isPressed();
        boolean lmb   = mc.options.attackKey.isPressed();
        boolean rmb   = mc.options.useKey.isPressed();

        drawKey(ctx, mc, "W",   baseX + 18, baseY,      w);
        drawKey(ctx, mc, "A",   baseX,      baseY + 14, a);
        drawKey(ctx, mc, "S",   baseX + 18, baseY + 14, s);
        drawKey(ctx, mc, "D",   baseX + 36, baseY + 14, d);
        drawWideKey(ctx, mc, "___", baseX, baseY + 28, space);
        drawKey(ctx, mc, "LMB", baseX,      baseY + 42, lmb);
        drawKey(ctx, mc, "RMB", baseX + 28, baseY + 42, rmb);

        if (SilveraConfig.cpsEnabled) {
            ctx.drawText(mc.textRenderer,
                "§7CPS: §c" + cps, baseX, baseY + 56, 0xFFFFFF, true);
        }
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc,
                                 String label, int x, int y, boolean pressed) {
        int bg     = pressed ? 0xCCFF2020 : 0xAA000000;
        int border = pressed ? 0xFFFF5555 : 0xFF444444;
        int text   = pressed ? 0xFFFFFFFF : 0xFFAAAAAA;
        ctx.fill(x - 1, y - 1, x + 15, y + 11, border);
        ctx.fill(x,     y,     x + 14, y + 10, bg);
        int tw = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, x + (14 - tw) / 2, y + 1, text, false);
    }

    private static void drawWideKey(DrawContext ctx, MinecraftClient mc,
                                     String label, int x, int y, boolean pressed) {
        int bg     = pressed ? 0xCCFF2020 : 0xAA000000;
        int border = pressed ? 0xFFFF5555 : 0xFF444444;
        int text   = pressed ? 0xFFFFFFFF : 0xFFAAAAAA;
        ctx.fill(x - 1, y - 1, x + 51, y + 11, border);
        ctx.fill(x,     y,     x + 50, y + 10, bg);
        int tw = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, x + (50 - tw) / 2, y + 1, text, false);
    }

    private static void renderStatsBar(DrawContext ctx, MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        int screenH = mc.getWindow().getScaledHeight();
        int x = 4, y = screenH - 24;

        int hp    = (int) p.getHealth();
        int maxHp = (int) p.getMaxHealth();
        int fps   = mc.getCurrentFps();

        ctx.fill(x - 2, y - 2, x + 100, y + 18, 0xAA000000);
        ctx.fill(x - 2, y - 2, x - 1,  y + 18, 0xFFFF2020);
        ctx.drawText(mc.textRenderer, "§c❤ " + hp + "/" + maxHp, x + 2, y + 2,  0xFFFFFF, true);
        if (SilveraConfig.fpsEnabled)
            ctx.drawText(mc.textRenderer, "§7FPS: §a" + fps,     x + 2, y + 11, 0xFFFFFF, true);

        // HP bar
        float ratio = maxHp > 0 ? (float) hp / maxHp : 0;
        ctx.fill(x + 2, y + 20, x + 98,                    y + 23, 0xFF333333);
        ctx.fill(x + 2, y + 20, x + 2 + (int)(96 * ratio), y + 23,
            ratio > 0.6f ? 0xFF44FF44 : ratio > 0.3f ? 0xFFFFAA00 : 0xFFFF2020);
    }
}
