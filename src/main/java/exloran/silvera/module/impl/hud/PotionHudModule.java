package com.silvera.module.impl.hud;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Collection;

/**
 * Potion HUD — aktif efektleri ve kalan sürelerini gösterir.
 */
public class PotionHudModule extends Module {

    public PotionHudModule() {
        super("Potion HUD", "Aktif efektleri ve sürelerini gösterir.", Category.HUD);
    }

    public void render(DrawContext ctx, int screenW, int screenH) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        if (effects.isEmpty()) return;

        int y = screenH / 2 - (effects.size() * 11) / 2;
        int x = 4;

        for (StatusEffectInstance eff : effects) {
            RegistryEntry<StatusEffect> type = eff.getEffectType();
            String name = type.getKey()
                    .map(k -> k.getValue().getPath().replace("_", " "))
                    .orElse("unknown");

            // Capitalize
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            int dur = eff.getDuration();
            String timeStr;
            if (dur > 20 * 60 * 60) {
                timeStr = "∞";
            } else {
                int secs = dur / 20;
                timeStr = secs / 60 + ":" + String.format("%02d", secs % 60);
            }

            // Renk: beneficial = yeşil, harmful = kırmızı
            boolean beneficial = type.value().isBeneficial();
            String color = beneficial ? "§a" : "§c";

            String line = color + name + " §7" + (eff.getAmplifier() + 1) + " §f" + timeStr;
            ctx.drawTextWithShadow(mc.textRenderer, line, x, y, 0xFFFFFF);
            y += 11;
        }
    }
}
