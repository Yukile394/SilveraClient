package com.silvera.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

public class HudRenderer {

    public static void register() {

        HudRenderCallback.EVENT.register((DrawContext context, float tickDelta) -> {
            ParticleHud.tick();
            ParticleHud.render(context);
        });

        System.out.println("HUD Registered");
    }
}
