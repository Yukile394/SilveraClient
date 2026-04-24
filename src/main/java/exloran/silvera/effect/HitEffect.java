package com.silvera.client.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class HitEffect {

    public static final List<HitCircle> circles = new ArrayList<>();
    public static final List<PetalParticle> petals = new ArrayList<>();
    private static final Random random = new Random();

    // Düşman hasar alınca çağrılır
    public static void spawnHitEffect(LivingEntity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        // Sadece oyuncunun vurduğu entity'ler için
        double dist = mc.player.distanceTo(entity);
        if (dist > 6.0) return;

        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() / 2.0;
        double z = entity.getZ();

        // Dönen daire efekti
        circles.add(new HitCircle(x, y, z));

        // Petal partiküller (8-12 adet)
        int count = 8 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            petals.add(new PetalParticle(x, y, z));
        }

        // Minecraft dust partikülleri (kırmızı)
        DustParticleEffect dust = new DustParticleEffect(
            new Vector3f(1.0f, 0.1f, 0.1f), 1.5f
        );
        for (int i = 0; i < 12; i++) {
            double vx = (random.nextDouble() - 0.5) * 0.3;
            double vy = random.nextDouble() * 0.2;
            double vz = (random.nextDouble() - 0.5) * 0.3;
            mc.world.addParticle(dust, x, y, z, vx, vy, vz);
        }
    }

    public static void tick() {
        Iterator<HitCircle> ci = circles.iterator();
        while (ci.hasNext()) {
            HitCircle c = ci.next();
            c.update();
            if (!c.isAlive()) ci.remove();
        }

        Iterator<PetalParticle> pi = petals.iterator();
        while (pi.hasNext()) {
            PetalParticle p = pi.next();
            p.update();
            if (!p.isAlive()) pi.remove();
        }
    }

    // ─── Dönen daire ───────────────────────────────────────────────────────
    public static class HitCircle {
        public double x, y, z;
        public int life = 20;
        public float radius = 0.3f;
        public float angle = 0f;

        HitCircle(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }

        void update() {
            radius += 0.12f;   // Dışa doğru genişle
            angle += 15f;      // Dön
            life--;
        }

        boolean isAlive() { return life > 0; }
        public float getAlpha() { return (float) life / 20f; }
    }

    // ─── Uçan petal partiküller ─────────────────────────────────────────────
    public static class PetalParticle {
        public double x, y, z;
        public double vx, vy, vz;
        public int life;
        public final int maxLife;
        private static final Random rng = new Random();

        PetalParticle(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
            double angle = rng.nextDouble() * Math.PI * 2;
            double speed = 0.05 + rng.nextDouble() * 0.15;
            this.vx = Math.cos(angle) * speed;
            this.vy = 0.05 + rng.nextDouble() * 0.1;
            this.vz = Math.sin(angle) * speed;
            this.maxLife = 15 + rng.nextInt(15);
            this.life = maxLife;
        }

        void update() {
            x += vx; y += vy; z += vz;
            vy -= 0.008; // yerçekimi
            life--;
        }

        boolean isAlive() { return life > 0; }
        public float getAlpha() { return (float) life / maxLife; }
    }
}
