package com.silvera.module.impl.visual;

import com.silvera.module.api.Module;

/**
 * Motion Blur — hızlı dönerken ekranda blur efekti.
 * GameRendererMixin shader pipeline üzerinden uygulanır.
 */
public class MotionBlurModule extends Module {

    public static float blurStrength = 0.6f; // 0.0 - 1.0

    public MotionBlurModule() {
        super("Motion Blur", "Kamera hareket ederken yumuşak blur efekti.", Category.VISUAL);
    }

    @Override
    public void onEnable()  { blurStrength = 0.6f; }
    @Override
    public void onDisable() { blurStrength = 0.0f; }
}
