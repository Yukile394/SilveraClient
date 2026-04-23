package com.silvera.client;

import net.fabricmc.api.ClientModInitializer;
import com.silvera.client.hud.HudRenderer;
import com.silvera.client.key.Keybinds;

public class SilveraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderer.register();
        Keybinds.register();
        System.out.println("Silvera Loaded.");
    }
}
