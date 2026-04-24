package com.silvera.client.hud;

import com.silvera.client.config.SilveraConfig;
import com.silvera.client.key.Keybinds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

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

    // ── Yardımcı: gradient panel (sol kırmızı → siyah) ─────────────────────
    private static void drawGradientPanel(DrawContext ctx, int x, int y, int w, int h) {
        Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Sol taraf: kırmızı tint
        buf.vertex(matrix, x,     y,     0).color(0.6f, 0.0f, 0.0f, 0.75f);
        buf.vertex(matrix, x,     y + h, 0).color(0.6f, 0.0f, 0.0f, 0.75f);
        // Sağ taraf: siyah
        buf.vertex(matrix, x + w, y + h, 0).color(0.0f, 0.0f, 0.0f, 0.75f);
        buf.vertex(matrix, x + w, y,     0).color(0.0f, 0.0f, 0.0f, 0.75f);

        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.disableBlend();
    }

    // ── Sol kırmızı çizgi ───────────────────────────────────────────────────
    private static void drawRedAccent(DrawContext ctx, int x, int y, int h) {
        Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Parlak kırmızı → soluk — glow efekti
        buf.vertex(matrix, x,     y,     0).color(1f, 0.1f, 0.1f, 1.0f);
        buf.vertex(matrix, x,     y + h, 0).color(1f, 0.1f, 0.1f, 1.0f);
        buf.vertex(matrix, x + 2, y + h, 0).color(1f, 0.1f, 0.1f, 0.3f);
        buf.vertex(matrix, x + 2, y,     0).color(1f, 0.1f, 0.1f, 0.3f);

        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.disableBlend();
    }

    // ── Smooth yuvarlak tuş (köşe simulasyonu) ─────────────────────────────
    private static void drawSmoothKey(DrawContext ctx, int x, int y,
                                       int w, int h, boolean pressed) {
        Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        float r = pressed ? 1f : 0.15f;
        float g = pressed ? 0.1f : 0.15f;
        float b = pressed ? 0.1f : 0.15f;
        float a = pressed ? 0.85f : 0.65f;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Ana gövde
        buf.vertex(matrix, x,     y,     0).color(r, g, b, a);
        buf.vertex(matrix, x,     y + h, 0).color(r * 0.6f, g, b, a);
        buf.vertex(matrix, x + w, y + h, 0).color(r * 0.6f, g, b, a);
        buf.vertex(matrix, x + w, y,     0).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        // Pressed iken glow border
        if (pressed) {
            drawGlowBorder(ctx, x, y, w, h);
        }

        RenderSystem.disableBlend();
    }

    private static void drawGlowBorder(DrawContext ctx, int x, int y, int w, int h) {
        Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tess = Tessellator.getInstance();

        // 4 kenar için ince parlak çizgi
        float[][] edges = {
            {x, y, x + w, y, x + w, y + 1, x, y + 1},           // üst
            {x, y + h - 1, x + w, y + h - 1, x + w, y + h, x, y + h}, // alt
            {x, y, x + 1, y, x + 1, y + h, x, y + h},           // sol
            {x + w - 1, y, x + w, y, x + w, y + h, x + w - 1, y + h}  // sağ
        };

        for (float[] e : edges) {
            BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buf.vertex(matrix, e[0], e[1], 0).color(1f, 0.3f, 0.3f, 0.9f);
            buf.vertex(matrix, e[2], e[3], 0).color(1f, 0.3f, 0.3f, 0.9f);
            buf.vertex(matrix, e[4], e[5], 0).color(1f, 0.3f, 0.3f, 0.9f);
            buf.vertex(matrix, e[6], e[7], 0).color(1f, 0.3f, 0.3f, 0.9f);
            BufferRenderer.drawWithGlobalProgram(buf.end());
        }
    }

    // ── WATERMARK ───────────────────────────────────────────────────────────
    private static void renderWatermark(DrawContext ctx, MinecraftClient mc) {
        String text = SilveraConfig.watermark;
        int x = 4, y = 4;
        int w = mc.textRenderer.getWidth(text) + 14;
        int h = 14;

        drawGradientPanel(ctx, x, y, w, h);
        drawRedAccent(ctx, x, y, h);
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

            drawGradientPanel(ctx, x, y, w, 12);
            // Sağ kırmızı aksan
            Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buf.vertex(matrix, x + w - 2, y,      0).color(1f, 0.1f, 0.1f, 0.3f);
            buf.vertex(matrix, x + w - 2, y + 12, 0).color(1f, 0.1f, 0.1f, 0.3f);
            buf.vertex(matrix, x + w,     y + 12, 0).color(1f, 0.1f, 0.1f, 1.0f);
            buf.vertex(matrix, x + w,     y,      0).color(1f, 0.1f, 0.1f, 1.0f);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            RenderSystem.disableBlend();

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

        drawSmoothKey(ctx, baseX + 19, baseY,      14, 12, w);
        drawSmoothKey(ctx, baseX,      baseY + 14, 14, 12, a);
        drawSmoothKey(ctx, baseX + 19, baseY + 14, 14, 12, s);
        drawSmoothKey(ctx, baseX + 38, baseY + 14, 14, 12, d);
        drawSmoothKey(ctx, baseX,      baseY + 28, 52, 10, space);
        drawSmoothKey(ctx, baseX,      baseY + 40, 24, 12, lmb);
        drawSmoothKey(ctx, baseX + 28, baseY + 40, 24, 12, rmb);

        // Tuş etiketleri
        drawKeyLabel(ctx, mc, "W",   baseX + 19, baseY,      14, 12, w);
        drawKeyLabel(ctx, mc, "A",   baseX,      baseY + 14, 14, 12, a);
        drawKeyLabel(ctx, mc, "S",   baseX + 19, baseY + 14, 14, 12, s);
        drawKeyLabel(ctx, mc, "D",   baseX + 38, baseY + 14, 14, 12, d);
        drawKeyLabel(ctx, mc, "▬",   baseX,      baseY + 28, 52, 10, space);
        drawKeyLabel(ctx, mc, "LMB", baseX,      baseY + 40, 24, 12, lmb);
        drawKeyLabel(ctx, mc, "RMB", baseX + 28, baseY + 40, 24, 12, rmb);

        if (SilveraConfig.cpsEnabled)
            ctx.drawText(mc.textRenderer,
                "§7" + cps + " §cCPS", baseX + 4, baseY + 55, 0xFFFFFF, true);
    }

    private static void drawKeyLabel(DrawContext ctx, MinecraftClient mc,
                                      String label, int x, int y,
                                      int w, int h, boolean pressed) {
        int color = pressed ? 0xFFFFFFFF : 0xFF888888;
        int tw = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, x + (w - tw) / 2, y + (h - 7) / 2, color, false);
    }

    // ── STATS BAR ───────────────────────────────────────────────────────────
    private static void renderStatsBar(DrawContext ctx, MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        int screenH = mc.getWindow().getScaledHeight();
        int x = 4, y = screenH - 26;

        int hp    = (int) p.getHealth();
        int maxHp = (int) p.getMaxHealth();
        int fps   = mc.getCurrentFps();

        drawGradientPanel(ctx, x, y, 102, 22);
        drawRedAccent(ctx, x, y, 22);

        ctx.drawText(mc.textRenderer, "§c❤ §f" + hp + "§7/" + maxHp, x + 5, y + 3,  0xFFFFFF, true);
        if (SilveraConfig.fpsEnabled)
            ctx.drawText(mc.textRenderer, "§7FPS §a" + fps, x + 5, y + 12, 0xFFFFFF, true);

        // Smooth HP bar
        float ratio = maxHp > 0 ? (float) hp / maxHp : 0;
        int barY = y + 22;
        drawHealthBar(ctx, x, barY, 102, ratio);
    }

    private static void drawHealthBar(DrawContext ctx, int x, int y, int w, float ratio) {
        Matrix4f matrix = ctx.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tess = Tessellator.getInstance();

        // Arka plan
        BufferBuilder bg = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bg.vertex(matrix, x,     y,     0).color(0.1f, 0.1f, 0.1f, 0.7f);
        bg.vertex(matrix, x,     y + 3, 0).color(0.1f, 0.1f, 0.1f, 0.7f);
        bg.vertex(matrix, x + w, y + 3, 0).color(0.1f, 0.1f, 0.1f, 0.7f);
        bg.vertex(matrix, x + w, y,     0).color(0.1f, 0.1f, 0.1f, 0.7f);
        BufferRenderer.drawWithGlobalProgram(bg.end());

        // Dolgu gradient
        float fw = w * ratio;
        float r1 = ratio > 0.6f ? 0.1f : ratio > 0.3f ? 0.9f : 1.0f;
        float g1 = ratio > 0.6f ? 0.9f : ratio > 0.3f ? 0.6f : 0.1f;

        BufferBuilder fill = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fill.vertex(matrix, x,      y,     0).color(r1, g1, 0.1f, 0.9f);
        fill.vertex(matrix, x,      y + 3, 0).color(r1 * 0.7f, g1 * 0.7f, 0.1f, 0.9f);
        fill.vertex(matrix, x + fw, y + 3, 0).color(r1 * 0.7f, g1 * 0.7f, 0.1f, 0.9f);
        fill.vertex(matrix, x + fw, y,     0).color(r1, g1, 0.1f, 0.9f);
        BufferRenderer.drawWithGlobalProgram(fill.end());

        RenderSystem.disableBlend();
    }
                       }
