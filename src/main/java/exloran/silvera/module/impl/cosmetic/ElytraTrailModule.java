package com.silvera.module.impl.cosmetic;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Elytra Trail — elytra ile uçarken spektaküler bir iz.
 */
public class ElytraTrailModule extends Module {

    private int tick = 0;

    public ElytraTrailModule() {
        super("Elytra Trail", "Elytra ile uçarken muhteşem bir iz bırakır.", Category.COSMETIC);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isFallFlying()) return;

        tick++;
        Vec3d pos = mc.player.getPos();
        Vec3d vel = mc.player.getVelocity();

        double x = pos.x;
        double y = pos.y + 0.5;
        double z = pos.z;

        // Ana iz - Dragon Breath (mor)
        for (int i = 0; i < 4; i++) {
            double ox = (Math.random() - 0.5) * 0.4;
            double oy = (Math.random() - 0.5) * 0.4;
            double oz = (Math.random() - 0.5) * 0.4;
            mc.world.addParticle(ParticleTypes.DRAGON_BREATH,
                    x + ox, y + oy, z + oz,
                    -vel.x * 0.5, -vel.y * 0.3, -vel.z * 0.5);
        }

        // END_ROD iz (beyaz parıltı)
        if (tick % 2 == 0) {
            mc.world.addParticle(ParticleTypes.END_ROD,
                    x + (Math.random() - 0.5) * 0.5,
                    y + (Math.random() - 0.5) * 0.3,
                    z + (Math.random() - 0.5) * 0.5,
                    -vel.x * 0.3, -vel.y * 0.1, -vel.z * 0.3);
        }

        // Kanat uçları - iki kenara partikül
        Vec3d right = new Vec3d(-vel.z, 0, vel.x).normalize().multiply(0.6);
        // Sol kanat
        mc.world.addParticle(ParticleTypes.PORTAL,
                x + right.x, y, z + right.z,
                -vel.x * 0.2, 0.02, -vel.z * 0.2);
        // Sağ kanat
        mc.world.addParticle(ParticleTypes.PORTAL,
                x - right.x, y, z - right.z,
                -vel.x * 0.2, 0.02, -vel.z * 0.2);

        // Hız arttıkça daha yoğun
        double speed = vel.length();
        if (speed > 0.8 && tick % 3 == 0) {
            for (int i = 0; i < 3; i++) {
                mc.world.addParticle(ParticleTypes.FLAME,
                        x + (Math.random() - 0.5) * 0.3,
                        y + (Math.random() - 0.5) * 0.3,
                        z + (Math.random() - 0.5) * 0.3,
                        -vel.x * 0.4, -vel.y * 0.2, -vel.z * 0.4);
            }
        }
    }
}
