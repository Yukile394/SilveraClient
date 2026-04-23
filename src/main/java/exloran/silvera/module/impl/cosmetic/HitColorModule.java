package com.silvera.module.impl.cosmetic;

import com.silvera.module.api.Module;

/**
 * Hit Color — vurulduğunda oyuncunun rengi değişir (kırmızı yerine özel renk).
 * LivingEntityRendererMixin bu flag'i okur.
 */
public class HitColorModule extends Module {

    // ARGB renk — default: parlak cyan
    public static int hitColor = 0xFF00FFFF;
    public static float hitFlashDuration = 0.4f;

    public HitColorModule() {
        super("Hit Color", "Vurulduğunda özel renk flash efekti.", Category.COSMETIC);
    }
}
