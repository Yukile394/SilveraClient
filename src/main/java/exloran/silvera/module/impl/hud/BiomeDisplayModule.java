package com.silvera.module.impl.hud;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

/**
 * Biome Display — mevcut biome adını gösterir.
 */
public class BiomeDisplayModule extends Module {

    public BiomeDisplayModule() {
        super("Biome Display", "Bulunduğun biome'u gösterir.", Category.HUD);
    }

    public void render(DrawContext ctx, int screenW, int screenH) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        RegistryEntry<Biome> biomeEntry = mc.world.getBiome(mc.player.getBlockPos());
        String biomeName = biomeEntry.getKey()
                .map(k -> k.getValue().getPath().replace("_", " "))
                .orElse("unknown");

        // Capitalize
        String display = "§b" + biomeName.substring(0, 1).toUpperCase() + biomeName.substring(1);
        int textW = mc.textRenderer.getWidth(display);
        ctx.drawTextWithShadow(mc.textRenderer, display, screenW - textW - 4, 14, 0xFFFFFF);
    }
}
