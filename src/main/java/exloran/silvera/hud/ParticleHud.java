package com.silvera.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleHud {

    private static final List<SoupParticle> particles = new ArrayList<>();
    private static final Random random = new Random();

    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            int screenW = mc.getWindow().getScaledWidth();
            int screenH = mc.getWindow().getScaledHeight();
            particles.add(new SoupParticle(
                random.nextDouble() * screenW,
                random.nextDouble() * screenH
            ));
        }

        particles.removeIf(p -> { p.update(); return !p.isAlive(); });
    }

    public static void render(DrawContext context) {
        if (particles.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Tessellator tess = Tessellator.getInstance();

        for (SoupParticle p : particles) {
            float alpha = p.getAlpha();
            if (alpha <= 0.01f) continue;

            float x = (float) p.x;
            float y = (float) p.y;
            float s = p.size;

            // Glow efekti: büyükten küçüğe 3 katman
            drawGlowCircle(tess, matrix, x, y, s * 3.0f, 1f, 0.1f, 0.1f, alpha * 0.15f);
            drawGlowCircle(tess, matrix, x, y, s * 1.8f, 1f, 0.2f, 0.2f, alpha * 0.35f);
            drawGlowCircle(tess, matrix, x, y, s,        1f, 0.6f, 0.6f, alpha * 0.9f);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    // Smooth daire — segment sayısı artınca mükemmel yuvarlak
    private static void drawGlowCircle(Tessellator tess, Matrix4f matrix,
                                        float cx, float cy, float radius,
                                        float r, float g, float b, float a) {
        if (a <= 0) return;
        int segments = 16;
        BufferBuilder buffer = tess.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        // Merkez nokta (parlak)
        buffer.vertex(matrix, cx, cy, 0).color(r, g, b, a);

        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float px = cx + (float)(Math.cos(angle) * radius);
            float py = cy + (float)(Math.sin(angle) * radius);
            // Kenar şeffaf — glow efekti
            buffer.vertex(matrix, px, py, 0).color(r, g, b, 0f);
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    static class SoupParticle {
        double x, y, vx, vy;
        int life;
        final int maxLife;
        final float size;

        SoupParticle(double x, double y) {
            this.x = x; this.y = y;
            this.maxLife = 40 + new Random().nextInt(60);
            this.life = maxLife;
            this.size = 1.5f + new Random().nextFloat() * 3f;
            double angle = new Random().nextDouble() * Math.PI * 2;
            double speed = 0.15 + new Random().nextDouble() * 0.4;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed - 0.25;
        }

        void update() { x += vx; vy += 0.02; y += vy; life--; }
        boolean isAlive() { return life > 0; }

        float getAlpha() {
            float ratio = (float) life / maxLife;
            if (ratio > 0.8f) return (1f - ratio) / 0.2f;
            if (ratio < 0.25f) return ratio / 0.25f;
            return 1f;
        }
    }
}
