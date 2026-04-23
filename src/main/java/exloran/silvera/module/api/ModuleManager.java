package com.silvera.module.api;

import com.silvera.module.impl.cosmetic.CapeAnimationModule;
import com.silvera.module.impl.cosmetic.ElytraTrailModule;
import com.silvera.module.impl.cosmetic.HitColorModule;
import com.silvera.module.impl.cosmetic.ZoomModule;
import com.silvera.module.impl.hud.ArmorHudModule;
import com.silvera.module.impl.hud.BiomeDisplayModule;
import com.silvera.module.impl.hud.CoordinatesModule;
import com.silvera.module.impl.hud.DirectionHudModule;
import com.silvera.module.impl.hud.FpsCounterModule;
import com.silvera.module.impl.hud.PotionHudModule;
import com.silvera.module.impl.particle.AmbientParticleModule;
import com.silvera.module.impl.particle.FootstepParticleModule;
import com.silvera.module.impl.particle.HitParticleModule;
import com.silvera.module.impl.particle.TrailParticleModule;
import com.silvera.module.impl.visual.MotionBlurModule;

import java.util.ArrayList;
import java.util.List;

/**
 * ModuleManager — tüm modülleri kayıt eder ve tick'ler.
 */
public class ModuleManager {

    private static final List<Module> modules = new ArrayList<>();

    public static void init() {
        // ── Particles ──
        register(new FootstepParticleModule());
        register(new TrailParticleModule());
        register(new AmbientParticleModule());
        register(new HitParticleModule());

        // ── HUD ──
        register(new CoordinatesModule());
        register(new FpsCounterModule());
        register(new BiomeDisplayModule());
        register(new DirectionHudModule());
        register(new ArmorHudModule());
        register(new PotionHudModule());

        // ── Visual ──
        register(new MotionBlurModule());

        // ── Cosmetic ──
        register(new ElytraTrailModule());
        register(new CapeAnimationModule());
        register(new HitColorModule());
        register(new ZoomModule());

        // Default olarak bazılarını aç
        get(CoordinatesModule.class).setEnabled(true);
        get(FpsCounterModule.class).setEnabled(true);
        get(DirectionHudModule.class).setEnabled(true);
        get(FootstepParticleModule.class).setEnabled(true);
    }

    public static void register(Module m) { modules.add(m); }

    public static List<Module> getAll() { return modules; }

    public static List<Module> getByCategory(Module.Category cat) {
        return modules.stream().filter(m -> m.getCategory() == cat).toList();
    }

    public static <T extends Module> T get(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) return clazz.cast(m);
        }
        return null;
    }

    public static void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick();
        }
    }
}
