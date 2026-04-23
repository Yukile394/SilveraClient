package com.silvera.module.impl.cosmetic;

import com.silvera.module.api.Module;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Zoom — C tuşuna basılı tutunca yakınlaştırır (smooth).
 * GameRendererMixin FOV'u override eder.
 */
public class ZoomModule extends Module {

    public static boolean isZooming = false;
    public static float zoomFov = 15.0f;         // Zoom'daki FOV
    public static float currentFovMultiplier = 1.0f; // 1.0 = normal, 0 = tam zoom
    private static final float ZOOM_SPEED = 0.12f;

    public static KeyBinding KEY_ZOOM;

    public ZoomModule() {
        super("Zoom", "C tuşuyla smooth zoom.", Category.COSMETIC);
    }

    @Override
    public void onEnable() {
        KEY_ZOOM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.silvera.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "key.categories.silvera"
        ));
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (KEY_ZOOM != null && KEY_ZOOM.isPressed()) {
            isZooming = true;
            currentFovMultiplier = Math.max(0.0f, currentFovMultiplier - ZOOM_SPEED);
        } else {
            isZooming = false;
            currentFovMultiplier = Math.min(1.0f, currentFovMultiplier + ZOOM_SPEED);
        }
    }

    /** GameRendererMixin çağırır */
    public static float getModifiedFov(float originalFov) {
        if (!isZooming && currentFovMultiplier >= 1.0f) return originalFov;
        float target = zoomFov;
        return target + (originalFov - target) * currentFovMultiplier;
    }
}
