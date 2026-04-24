package com.silvera.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleHud {

    private static final List<SoupParticle> particles = new ArrayList<>();
    private static final Random random = new Random();

    // Her tick'te yeni partiküller spawn et
    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Her tick'te 1-3 rastgele partikül ekle
        int spawnCount = random.nextInt(3) + 1;
        for (int i = 0; i < spawnCount; i++) {
            spawnParticle(mc);
        }

        // Ölü partikülleri kaldır + güncelle
        Iterator<SoupParticle> iter = particles.iterator();
        while (iter.hasNext()) {
            SoupParticle p = iter.next();
            p.update();
            if (!p.isAlive()) {
                iter.remove();
            }
        }
    }

    private static void spawnParticle(MinecraftClient mc) {
        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        // Ekranın rastgele bir noktasından spawn
        double x = random.nextDouble() * screenW;
        double y = random.nextDouble() * screenH;

        // Farklı partikül tipleri
        int type = random.nextInt(4);
        particles.add(new SoupParticle(x, y, type));
    }

    public static void render(DrawContext context) {
        for (SoupParticle p : particles) {
            p.draw(context);
        }
    }

    // ─────────────────────────────────────────────
    // İç sınıf: SoupParticle
    // ─────────────────────────────────────────────
    static class SoupParticle {

        double x, y;
        double vx, vy;          // Hız vektörü
        int life;
        final int maxLife;
        final int type;          // 0=yıldız 1=kalp 2=nokta 3=kare
        float size;
        final Random rng = new Random();

        // Soup Visual renk paleti: kırmızı tonları
        private static final int[] COLORS = {
            0xFFFF2020,   // parlak kırmızı
            0xFFCC0000,   // koyu kırmızı
            0xFFFF5555,   // açık kırmızı
            0xFFFF0040,   // pembe-kırmızı
            0xFFFFFFFF,   // beyaz aksan
            0xFFAA0000,   // kan kırmızısı
        };

        SoupParticle(double x, double y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.maxLife = 40 + rng.nextInt(60);   // 40-100 tick ömür
            this.life = maxLife;
            this.size = 1.0f + rng.nextFloat() * 2.5f;

            // Rastgele hareket yönü
            double angle = rng.nextDouble() * Math.PI * 2;
            double speed = 0.3 + rng.nextDouble() * 0.8;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed - 0.5; // Biraz yukarı eğimli
        }

        void update() {
            x += vx;
            vy += 0.04;   // Yerçekimi
            y += vy;
            life--;
        }

        boolean isAlive() {
            return life > 0;
        }

        // Alpha: yaşam süresine göre fade-in/fade-out
        int getAlpha() {
            float ratio = (float) life / maxLife;
            // İlk %20'de fade-in, son %30'da fade-out
            float alpha;
            if (ratio > 0.8f) {
                alpha = (1.0f - ratio) / 0.2f;
            } else if (ratio < 0.3f) {
                alpha = ratio / 0.3f;
            } else {
                alpha = 1.0f;
            }
            return (int) (alpha * 255);
        }

        int getColor() {
            int base = COLORS[rng.nextInt(COLORS.length)]; // sabit renk almak için ctor'da seçmek daha iyi ama bu da çalışır
            int alpha = getAlpha();
            return (alpha << 24) | (base & 0x00FFFFFF);
        }

        void draw(DrawContext context) {
            MinecraftClient mc = MinecraftClient.getInstance();
            int alpha = getAlpha();
            if (alpha <= 0) return;

            // Her partikül tipine göre renk sabit seçimi (ctor'da yapılmış gibi index'i life'a bağla)
            int colorIndex = (type + (maxLife - life)) % COLORS.length;
            int baseColor = COLORS[colorIndex];
            int color = (alpha << 24) | (baseColor & 0x00FFFFFF);

            int ix = (int) x;
            int iy = (int) y;
            int s = Math.max(1, (int) size);

            switch (type) {
                case 0 -> // Yıldız / artı şekli
                    drawStar(context, mc, ix, iy, s, color);

                case 1 -> // Kalp karakteri
                    context.drawText(mc.textRenderer, "♥", ix, iy, color, true);

                case 2 -> // Dolu nokta
                    context.fill(ix, iy, ix + s, iy + s, color);

                case 3 -> // Küçük kare outline
                    drawSquareOutline(context, ix, iy, s + 1, color);
            }
        }

        private void drawStar(DrawContext context, MinecraftClient mc, int x, int y, int s, int color) {
            // Artı / haç şeklinde küçük yıldız
            context.fill(x - s, y, x + s + 1, y + 1, color);  // yatay
            context.fill(x, y - s, x + 1, y + s + 1, color);  // dikey
        }

        private void drawSquareOutline(DrawContext context, int x, int y, int s, int color) {
            context.fill(x, y, x + s, y + 1, color);           // üst kenar
            context.fill(x, y + s - 1, x + s, y + s, color);  // alt kenar
            context.fill(x, y, x + 1, y + s, color);           // sol kenar
            context.fill(x + s - 1, y, x + s, y + s, color);  // sağ kenar
        }
    }
}
