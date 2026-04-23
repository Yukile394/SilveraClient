package com.silvera.module.impl.cosmetic;

import com.silvera.module.api.Module;

/**
 * Cape Animation — pelerin dalgalanma animasyonunu güzelleştirir.
 * Gerçek uygulama PlayerEntityRendererMixin üzerinden yapılır.
 * Bu modül sadece flag tutar, Mixin bunu okur.
 */
public class CapeAnimationModule extends Module {

    public static float capeFlutterIntensity = 1.8f;
    public static float capeSwingSpeed = 1.3f;

    public CapeAnimationModule() {
        super("Cape Animation", "Pelerinin animasyonunu daha akıcı ve dramatik yapar.", Category.COSMETIC);
    }

    @Override
    public void onEnable() {
        capeFlutterIntensity = 1.8f;
        capeSwingSpeed = 1.3f;
    }

    @Override
    public void onDisable() {
        capeFlutterIntensity = 1.0f;
        capeSwingSpeed = 1.0f;
    }
}
