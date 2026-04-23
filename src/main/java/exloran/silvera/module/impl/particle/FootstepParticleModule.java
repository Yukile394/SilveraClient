package com.silvera.module.impl.particle;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Footstep Particles — oyuncu yürürken ayak izi partikülleri
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

        // ❌ isFlying() yok 1.21
        // ✅ doğru kullanım
        if (mc.player.getAbilities().flying || mc.player.isFallFlying()) return;

        if (!mc.player.isOnGround()) return;

        tickCounter++;
        if (tickCounter < 4) return; // her 4 tick
        tickCounter = 0;

        Vec3d pos = mc.player.getPos();

        if (lastPos != null && pos.distanceTo(lastPos) < 0.1) return;
        lastPos = pos;

        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        // doğru sağ/sol ayak hesabı
        Vec3d right = mc.player.getRotationVec(1.0f)
                .rotateY((float) Math.toRadians(90));

        boolean leftStep = (System.currentTimeMillis() / 200) % 2 == 0;

        double side = leftStep ? 0.2 : -0.2;

        mc.world.addParticle(
                ParticleTypes.END_ROD,
                x + right.x * side, y + 0.05, z + right.z * side,
                0, 0.02, 0
        );

        mc.world.addParticle(
                ParticleTypes.WITCH,
                x + right.x * side, y + 0.05, z + right.z * side,
                0, 0.01, 0
        );
    }
}
