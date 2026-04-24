package com.silvera.client.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TrailEffect {

    private static final List<TrailParticle> trails = new ArrayList<>();
    private static final Random random = new Random();

    // Oyuncu her tick'te bu metodu çağırır
    public static void spawnTrail(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        // Sadece hareket ediyorken trail spawn et
        Vec3d vel = player.getVelocity();
        double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (speed < 0.05) return;

        double x = player.getX() + (random.nextDouble() - 0.5) * 0.3;
        double y = player.getY() + 0.1;
        double z = player.getZ() + (random.nextDouble() - 0.5) * 0.3;

        trails.add(new TrailParticle(x, y, z));

        // Minecraft'ın kendi partikül sistemine de ekle (3D dünya efekti)
        mc.world.addParticle(
            ParticleTypes.DUST,
            x, y + 0.1, z,
            0.0, 0.05, 0.0
        );
    }

    public static void tick() {
        Iterator<TrailParticle> iter = trails.iterator();
        while (iter.hasNext()) {
            TrailParticle p = iter.next();
            p.update();
            if (!p.isAlive()) iter.remove();
        }
    }

    public static List<TrailParticle> getTrails() {
        return trails;
    }

    // Trail partikülleri (2D HUD'da değil, 3D world'de render için veri tutar)
    public static class TrailParticle {
        public double x, y, z;
        public int life;
        public final int maxLife;

        TrailParticle(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.maxLife = 15 + new Random().nextInt(10);
            this.life = maxLife;
        }

        void update() { life--; }
        public boolean isAlive() { return life > 0; }
        public float getAlpha() { return (float) life / maxLife; }
    }
}
