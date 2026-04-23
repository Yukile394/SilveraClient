package com.silvera.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class HudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();

            drawContext.drawText(
                mc.textRenderer,
                "Silvera Client",
                5,
                5,
                0xFFFFFF,
                true
            );
        });
    }
}
