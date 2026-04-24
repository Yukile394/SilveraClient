package com.silvera.client.key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static KeyBinding toggleHud;
    public static boolean hudEnabled = true;

    public static void register() {

        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.silvera.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.silvera"
        ));
    }
}
