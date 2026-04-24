package com.silvera.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.util.math.MatrixStack;

public class HudRenderer {

    public static void register() {

        ParticleHud.init();

        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            ParticleHud.tick();
            ParticleHud.render(matrices);
        });

        System.out.println("HUD Registered");
    }
}
