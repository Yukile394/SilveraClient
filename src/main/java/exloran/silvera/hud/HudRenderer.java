package com.silvera.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudRenderer {

    public static void register() {

        HudRenderCallback.EVENT.register((context, tickDelta) -> {

            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null) return;

            int fps = client.getCurrentFps();

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
                    "FPS: " + fps,
                    10,
                    22,
                    0xFFFFFF,
                    true
            );
        });
    }
}
