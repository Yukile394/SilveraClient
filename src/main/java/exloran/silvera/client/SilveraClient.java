package exloran.silvera.client;

import net.fabricmc.api.ClientModInitializer;
import exloran.silvera.hud.HudRenderer;
import exloran.silvera.client.key.Keybinds;

public class SilveraClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderer.register();
        Keybinds.register();
        System.out.println("Silvera Loaded.");
    }
}
