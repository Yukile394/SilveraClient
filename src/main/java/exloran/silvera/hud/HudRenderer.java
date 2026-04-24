package com.silvera.client.hud;

import com.silvera.client.config.SilveraConfig;
import com.silvera.client.key.Keybinds;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

public class HudRenderer {

    // ─── CPS Sayacı (basit versiyon) ───────────────────────────────────────
    private static int cps = 0;
    private static int clickBuffer = 0;
    private static long lastCpsTick = 0;

    public static void onLeftClick() {
        clickBuffer++;
    }

    // ─── Keybind Dinleyicisi ────────────────────────────────────────────────
    private static void handleKeybinds() {
        if (Keybinds.toggleHud.wasPressed()) {
            SilveraConfig.hudEnabled = !SilveraConfig.hudEnabled;
        }
    }

    // ─── Register ──────────────────────────────────────────────────────────
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            handleKeybinds();
            updateCps();

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) return;

            // Particle tick + render
            ParticleHud.tick();
            ParticleHud.render(drawContext);

            if (!SilveraConfig.hudEnabled) return;

            // HUD bileşenleri
            renderWatermark(drawContext, mc);
            renderArrayList(drawContext, mc);
            renderKeystrokes(drawContext, mc);
            renderStatsBar(drawContext, mc);
        });
    }

    // ─── CPS Güncelle (yaklaşık 1 saniyede bir) ────────────────────────────
    private static void updateCps() {
        long now = System.currentTimeMillis();
        if (now - lastCpsTick >= 1000) {
            cps = clickBuffer;
            clickBuffer = 0;
            lastCpsTick = now;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // WATERMARK  —  sol üst köşe, kırmızı parlayan başlık
    // ═══════════════════════════════════════════════════════════════════════
    private static void renderWatermark(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.hudEnabled) return;

        String text = SilveraConfig.watermark + " §7v1.0";
        int x = 4;
        int y = 4;
        int w = mc.textRenderer.getWidth(text) + 8;

        // Arka plan: yarı saydam siyah panel
        ctx.fill(x - 2, y - 2, x + w, y + 12, 0xAA000000);
        // Sol kırmızı çizgi (Soup Visual tarzı aksan)
        ctx.fill(x - 2, y - 2, x - 1, y + 12, 0xFFFF2020);

        ctx.drawText(mc.textRenderer, "§c" + SilveraConfig.watermark + " §7v1.0", x + 3, y + 2, 0xFFFFFF, true);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ARRAYLIST  —  sağ üst, aktif modülleri listele
    // ═══════════════════════════════════════════════════════════════════════
    private static void renderArrayList(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.arrayListEnabled) return;

        String[] modules = { "ParticleHud", "Keystrokes", "CPS", "FPS", "Watermark" };

        int screenW = mc.getWindow().getScaledWidth();
        int y = 4;

        for (String mod : modules) {
            int textW = mc.textRenderer.getWidth(mod);
            int x = screenW - textW - 6;

            // Panel arka planı
            ctx.fill(x - 2, y - 1, screenW - 2, y + 9, 0xAA000000);
            // Sağ kırmızı aksan çizgisi
            ctx.fill(screenW - 3, y - 1, screenW - 2, y + 9, 0xFFFF2020);

            ctx.drawText(mc.textRenderer, "§c" + mod, x, y + 1, 0xFFFFFF, true);
            y += 12;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // KEYSTROKES  —  sağ alt köşe, WASD + boşluk + fare tuşları
    // ═══════════════════════════════════════════════════════════════════════
    private static void renderKeystrokes(DrawContext ctx, MinecraftClient mc) {
        if (!SilveraConfig.keystrokesEnabled) return;

        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        // Pozisyon: sağ alt
        int baseX = screenW - 58;
        int baseY = screenH - 68;

        boolean w = mc.options.forwardKey.isPressed();
        boolean s = mc.options.backKey.isPressed();
        boolean a = mc.options.leftKey.isPressed();
        boolean d = mc.options.rightKey.isPressed();
        boolean space = mc.options.jumpKey.isPressed();
        boolean lmb = mc.options.attackKey.isPressed();
        boolean rmb = mc.options.useKey.isPressed();

        // W
        drawKey(ctx, mc, "W", baseX + 18, baseY, w);
        // A S D
        drawKey(ctx, mc, "A", baseX, baseY + 14, a);
        drawKey(ctx, mc, "S", baseX + 18, baseY + 14, s);
        drawKey(ctx, mc, "D", baseX + 36, baseY + 14, d);
        // SPACE (geniş)
        drawWideKey(ctx, mc, "SPACE", baseX, baseY + 28, space);
        // LMB / RMB
        drawKey(ctx, mc, "LMB", baseX, baseY + 42, lmb);
        drawKey(ctx, mc, "RMB", baseX + 28, baseY + 42, rmb);

        // CPS göster
        if (SilveraConfig.cpsEnabled) {
            String cpsText = "§7CPS: §c" + cps;
            ctx.drawText(mc.textRenderer, cpsText, baseX, baseY + 56, 0xFFFFFF, true);
        }
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc, String label,
                                 int x, int y, boolean pressed) {
        int bgColor = pressed ? 0xCCFF2020 : 0xAA000000;
        int textColor = pressed ? 0xFFFFFFFF : 0xFFAAAAAA;
        int borderColor = pressed ? 0xFFFF5555 : 0xFF555555;

        // Border
        ctx.fill(x - 1, y - 1, x + 15, y + 11, borderColor);
        // Arka plan
        ctx.fill(x, y, x + 14, y + 10, bgColor);

        int textW = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, x + (14 - textW) / 2, y + 1, textColor, false);
    }

    private static void drawWideKey(DrawContext ctx, MinecraftClient mc, String label,
                                     int x, int y, boolean pressed) {
        int bgColor = pressed ? 0xCCFF2020 : 0xAA000000;
        int textColor = pressed ? 0xFFFFFFFF : 0xFFAAAAAA;
        int borderColor = pressed ? 0xFFFF5555 : 0xFF555555;

        ctx.fill(x - 1, y - 1, x + 51, y + 11, borderColor);
        ctx.fill(x, y, x + 50, y + 10, bgColor);

        int textW = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, x + (50 - textW) / 2, y + 1, textColor, false);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STATS BAR  —  sol alt köşe, HP / FPS / Ping
    // ═══════════════════════════════════════════════════════════════════════
    private static void renderStatsBar(DrawContext ctx, MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        int screenH = mc.getWindow().getScaledHeight();
        int x = 4;
        int y = screenH - 24;

        // HP
        int hp = (int) p.getHealth();
        int maxHp = (int) p.getMaxHealth();
        String hpText = "§c❤ " + hp + "/" + maxHp;

        // FPS
        String fpsText = SilveraConfig.fpsEnabled
            ? "§7FPS: §a" + mc.getCurrentFps()
            : "";

        // Panel
        int panelW = 100;
        ctx.fill(x - 2, y - 2, x + panelW, y + 18, 0xAA000000);
        ctx.fill(x - 2, y - 2, x - 1, y + 18, 0xFFFF2020); // sol kırmızı çizgi

        ctx.drawText(mc.textRenderer, hpText, x + 2, y + 2, 0xFFFFFF, true);
        if (!fpsText.isEmpty()) {
            ctx.drawText(mc.textRenderer, fpsText, x + 2, y + 11, 0xFFFFFF, true);
        }

        // HP bar
        renderHealthBar(ctx, x + 2, y + 20, 96, hp, maxHp);
    }

    private static void renderHealthBar(DrawContext ctx, int x, int y, int w, int hp, int maxHp) {
        if (maxHp <= 0) return;
        float ratio = (float) hp / maxHp;

        // Arka plan
        ctx.fill(x, y, x + w, y + 3, 0xFF333333);
        // Dolgu — yeşilden kırmızıya geçiş
        int fillColor;
        if (ratio > 0.6f)       fillColor = 0xFF44FF44;
        else if (ratio > 0.3f)  fillColor = 0xFFFFAA00;
        else                    fillColor = 0xFFFF2020;

        ctx.fill(x, y, x + (int)(w * ratio), y + 3, fillColor);
    }
}
