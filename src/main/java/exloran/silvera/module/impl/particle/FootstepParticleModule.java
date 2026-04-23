package com.silvera.module.impl.particle;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Footstep Particles — oyuncu yürürken ayak izleri bırakır.
 */
public class FootstepParticleModule extends Module {

    private int tickCounter = 0;
    private Vec3d lastPos = null;

    public FootstepParticleModule() {
        super("Footstep Particles", "Yürürken renkli partiküller bırakır.", Category.PARTICLES);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (mc.player.isFlying() || mc.player.isFallFlying()) return;
        if (!mc.player.isOnGround()) return;

        tickCounter++;
        if (tickCounter < 4) return; // Her 4 tikte bir
        tickCounter = 0;

        Vec3d pos = mc.player.getPos();
        if (lastPos != null && pos.distanceTo(lastPos) < 0.1) return; // Duruyorsa spawn etme
        lastPos = pos;

        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        // İki ayak pozisyonu (sol/sağ)
        double side = (tickCounter / 4 % 2 == 0) ? 0.2 : -0.2;
        Vec3d right = mc.player.getRotationVec(1.0f).rotateY((float) Math.toRadians(90));

        mc.world.addParticle(ParticleTypes.END_ROD,
                x + right.x * side, y + 0.05, z + right.z * side,
                0, 0.02, 0);
        mc.world.addParticle(ParticleTypes.WITCH,
                x + right.x * side, y + 0.05, z + right.z * side,
                0, 0.01, 0);
    }
}
