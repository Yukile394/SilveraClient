package com.silvera.module.impl.particle;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Hit Particles — bir şeye vurduğunda patlayan güzel partiküller.
 * WorldRendererMixin'den tetiklenir.
 */
public class HitParticleModule extends Module {

    public HitParticleModule() {
        super("Hit Particles", "Vurduğunda patlayan özel partiküller.", Category.PARTICLES);
    }

    /** Mixin'den çağrılır - hit pozisyonuna partikül yağdırır */
    public static void spawnHitEffect(double x, double y, double z) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        // Merkez patlama
        for (int i = 0; i < 8; i++) {
            double vx = (Math.random() - 0.5) * 0.4;
            double vy = (Math.random()) * 0.3 + 0.1;
            double vz = (Math.random() - 0.5) * 0.4;
            mc.world.addParticle(ParticleTypes.END_ROD, x, y, z, vx, vy, vz);
        }

        // Sparkle ring
        for (int i = 0; i < 6; i++) {
            double angle = (Math.PI * 2 / 6) * i;
            double vx = Math.cos(angle) * 0.2;
            double vz = Math.sin(angle) * 0.2;
            mc.world.addParticle(ParticleTypes.ENCHANT, x, y + 0.5, z, vx, 0.05, vz);
        }

        // Crit efekt
        mc.world.addParticle(ParticleTypes.CRIT, x, y + 0.3, z, 0, 0.1, 0);
    }
}
