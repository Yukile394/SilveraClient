package com.silvera.module.impl.particle;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Trail Particles — hareket ederken arkada iz bırakır.
 */
public class TrailParticleModule extends Module {

    private int tick = 0;

    public TrailParticleModule() {
        super("Trail Particles", "Hareket ederken güzel bir iz bırakır.", Category.PARTICLES);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        tick++;
        if (tick % 2 != 0) return;

        Vec3d pos = mc.player.getPos();
        Vec3d vel = mc.player.getVelocity();
        double speed = vel.horizontalLength();

        if (speed < 0.05) return; // Duruyorsa çizme

        double x = pos.x;
        double y = pos.y + 0.1;
        double z = pos.z;

        // Elytra ile uçuyorsa özel iz
        if (mc.player.isFallFlying()) {
            for (int i = 0; i < 3; i++) {
                double ox = (Math.random() - 0.5) * 0.3;
                double oy = (Math.random() - 0.5) * 0.3;
                double oz = (Math.random() - 0.5) * 0.3;
                mc.world.addParticle(ParticleTypes.DRAGON_BREATH, x + ox, y + oy, z + oz, 0, 0, 0);
                mc.world.addParticle(ParticleTypes.END_ROD, x + ox, y + oy + 0.1, z + oz, 0, 0.01, 0);
            }
        } else {
            // Normal koşu izi
            mc.world.addParticle(ParticleTypes.ENCHANT, x, y, z, 0, 0.05, 0);
            mc.world.addParticle(ParticleTypes.PORTAL, x, y, z,
                    (Math.random() - 0.5) * 0.1, 0.03, (Math.random() - 0.5) * 0.1);
        }
    }
}
