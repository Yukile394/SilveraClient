package com.silvera.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.*;

public class ParticleHud {

    private static final List<SoupParticle> particles = new ArrayList<>();
    private static final Random random = new Random();

    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Her tick 1-2 partikül — az ama kaliteli
        for (int i = 0; i < random.nextInt(2) + 1; i++) {
            int screenW = mc.getWindow().getScaledWidth();
            int screenH = mc.getWindow().getScaledHeight();
            double x = random.nextDouble() * screenW;
            double y = random.nextDouble() * screenH;
            // Sadece 2 tip: yıldız ve kare outline — kalp YOK
            particles.add(new SoupParticle(x, y, random.nextInt(2)));
        }

        particles.removeIf(p -> { p.update(); return !p.isAlive(); });
    }

    public static void render(DrawContext context) {
        for (SoupParticle p : particles) p.draw(context);
    }

    static class SoupParticle {
        double x, y, vx, vy;
        int life;
        final int maxLife;
        final int type;
        final float size;
        // Sadece kırmızı tonlar
        static final int[] COLORS = {
            0xFFFF2020, 0xFFCC0000, 0xFFFF5555, 0xFFAA0000
        };
        final int color;

        SoupParticle(double x, double y, int type) {
            this.x = x; this.y = y; this.type = type;
            this.maxLife = 50 + new Random().nextInt(50);
            this.life = maxLife;
            this.size = 1.0f + new Random().nextFloat() * 1.5f;
            double angle = new Random().nextDouble() * Math.PI * 2;
            double speed = 0.2 + new Random().nextDouble() * 0.5;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed - 0.3;
            this.color = COLORS[new Random().nextInt(COLORS.length)];
        }

        void update() { x += vx; vy += 0.03; y += vy; life--; }
        boolean isAlive() { return life > 0; }

        int getColor() {
            float ratio = (float) life / maxLife;
            float alpha = ratio > 0.8f ? (1f - ratio) / 0.2f
                        : ratio < 0.3f ? ratio / 0.3f : 1f;
            return ((int)(alpha * 255) << 24) | (color & 0x00FFFFFF);
        }

        void draw(DrawContext ctx) {
            int c = getColor();
            if ((c >>> 24) == 0) return;
            int ix = (int) x, iy = (int) y, s = Math.max(1, (int) size);
            if (type == 0) { // artı/yıldız
                ctx.fill(ix - s, iy, ix + s + 1, iy + 1, c);
                ctx.fill(ix, iy - s, ix + 1, iy + s + 1, c);
            } else { // kare outline
                ctx.fill(ix,         iy,         ix + s, iy + 1, c);
                ctx.fill(ix,         iy + s - 1, ix + s, iy + s, c);
                ctx.fill(ix,         iy,         ix + 1, iy + s, c);
                ctx.fill(ix + s - 1, iy,         ix + s, iy + s, c);
            }
        }
    }
}
