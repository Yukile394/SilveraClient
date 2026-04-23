package com.silvera.client;

import com.silvera.gui.SilveraMenuScreen;
import com.silvera.module.api.ModuleManager;
import com.silvera.module.impl.cosmetic.ZoomModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ╔══════════════════════════════════════╗
 * ║        Silvera Client v1.0.0         ║
 * ║   Fabric 1.21.x — Cosmetic Client   ║
 * ╚══════════════════════════════════════╝
 *
 * TUŞLAR:
 *   RShift → Mod menüsü aç/kapat
 *   C      → Zoom (ZoomModule aktifken)
 */
public class SilveraClient implements ClientModInitializer {

    public static final String NAME    = "Silvera Client";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER  = LoggerFactory.getLogger("Silvera");

    public static KeyBinding KEY_OPEN_MENU;

    @Override
    public void onInitializeClient() {
        LOGGER.info("╔══════════════════════════╗");
        LOGGER.info("║   Silvera Client v{}    ║", VERSION);
        LOGGER.info("╚══════════════════════════╝");

        // Menü tuşu → RShift
        KEY_OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.silvera.openmenu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.silvera"
        ));

        // Zoom tuşu kayıt → ZoomModule init içinde ama erken kayıt için burada da yapıyoruz
        // (ZoomModule kendi onEnable'ında handle eder)

        // Modülleri başlat
        ModuleManager.init();

        // Tick listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Menü açma
            while (KEY_OPEN_MENU.wasPressed()) {
                if (client.currentScreen instanceof SilveraMenuScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new SilveraMenuScreen());
                }
            }

            // Tüm modülleri tick'le
            ModuleManager.onTick();
        });

        LOGGER.info("[Silvera] Yüklendi! RShift = Menü");
    }
}
