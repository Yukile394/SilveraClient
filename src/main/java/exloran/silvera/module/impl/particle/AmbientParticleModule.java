package com.silvera.module.impl.particle;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Ambient Particles — oyuncunun etrafında sürekli yüzen partiküller.
 */
public class AmbientParticleModule extends Module {

    private int tick = 0;
    private double angle = 0;

    public AmbientParticleModule() {
        super("Ambient Particles", "Oyuncunun etrafında yüzen ambient partiküller.", Category.PARTICLES);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        tick++;
        angle += 0.15;

        Vec3d pos = mc.player.getPos();
        double cx = pos.x;
        double cy = pos.y + 1.0;
        double cz = pos.z;

        // Her 3 tikte bir yeni partikül
        if (tick % 3 == 0) {
            double radius = 0.8;
            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            double py = cy + Math.sin(angle * 2) * 0.3;

            mc.world.addParticle(ParticleTypes.END_ROD, px, py, pz, 0, 0.01, 0);
        }

        // Her 6 tikte sparkle
        if (tick % 6 == 0) {
            double radius2 = 1.1;
            double px2 = cx + Math.cos(angle + Math.PI) * radius2;
            double pz2 = cz + Math.sin(angle + Math.PI) * radius2;
            double py2 = cy + Math.sin(angle * 2 + Math.PI) * 0.3;
            mc.world.addParticle(ParticleTypes.WITCH, px2, py2, pz2, 0, 0.01, 0);
        }

        // Her 20 tikte büyük patlama
        if (tick % 20 == 0) {
            mc.world.addParticle(ParticleTypes.FLASH,
                    cx + (Math.random() - 0.5) * 0.5,
                    cy + (Math.random() - 0.5) * 0.5,
                    cz + (Math.random() - 0.5) * 0.5,
                    0, 0, 0);
        }
    }
}
