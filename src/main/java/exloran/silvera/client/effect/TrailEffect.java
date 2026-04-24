package com.silvera.client.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TrailEffect {

    private static final List<TrailParticle> trails = new ArrayList<>();
    private static final Random random = new Random();

    public static void spawnTrail(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        Vec3d vel = player.getVelocity();
        double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (speed < 0.05) return;

        double x = player.getX() + (random.nextDouble() - 0.5) * 0.3;
        double y = player.getY() + 0.1;
        double z = player.getZ() + (random.nextDouble() - 0.5) * 0.3;

        trails.add(new TrailParticle(x, y, z));

        Vector3f color = new Vector3f(1.0f, 0.1f, 0.1f);
        DustParticleEffect dust = new DustParticleEffect(color, 1.2f);
        mc.world.addParticle(dust, x, y + 0.1, z, 0.0, 0.05, 0.0);
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

    public static class TrailParticle {
        public double x, y, z;
        public int life;
        public final int maxLife;

        TrailParticle(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.maxLife = 15 + random.nextInt(10);
            this.life = maxLife;
        }

        void update() { life--; }
        public boolean isAlive() { return life > 0; }
        public float getAlpha() { return (float) life / maxLife; }
    }
}
