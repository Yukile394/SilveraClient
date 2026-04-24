package com.silvera.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudRenderer {

    public static void register() {

        HudRenderCallback.EVENT.register((DrawContext context, float tickDelta) -> {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null) return;

            context.drawText(
                    client.textRenderer,
                    "§bSilvera Client",
                    10,
                    10,
                    0xFFFFFF,
                    true
            );

            context.drawText(
                    client.textRenderer,
                    "FPS: " + MinecraftClient.getCurrentFps(),
                    10,
                    20,
                    0xFFFFFF,
                    true
            );
        });
    }
}
