package com.silvera.module.impl.particle;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Footstep Particles — SAFE VERSION (Pojav + 1.21 compatible)
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

        // 🔥 SAFE GUARDS (CRASH FIX)
        if (mc == null) return;
        if (mc.player == null) return;
        if (mc.world == null) return;
        if (mc.getNetworkHandler() == null) return;

        // ❌ 1.21 fix (isFlying removed)
        if (mc.player.getAbilities().flying || mc.player.isFallFlying()) return;

        if (!mc.player.isOnGround()) return;

        tickCounter++;
        if (tickCounter < 4) return;
        tickCounter = 0;

        Vec3d pos = mc.player.getPos();

        if (lastPos != null && pos.distanceTo(lastPos) < 0.1) return;
        lastPos = pos;

        Vec3d right = mc.player.getRotationVec(1.0f)
                .rotateY((float) Math.toRadians(90));

        boolean leftStep = (System.currentTimeMillis() / 200) % 2 == 0;
        double side = leftStep ? 0.2 : -0.2;

        mc.world.addParticle(
                ParticleTypes.END_ROD,
                pos.x + right.x * side,
                pos.y + 0.05,
                pos.z + right.z * side,
                0, 0.02, 0
        );

        mc.world.addParticle(
                ParticleTypes.WITCH,
                pos.x + right.x * side,
                pos.y + 0.05,
                pos.z + right.z * side,
                0, 0.01, 0
        );
    }
}
