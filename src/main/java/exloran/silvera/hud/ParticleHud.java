package com.silvera.client.hud;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ParticleHud {

    private static final List<Particle> particles = new ArrayList<>();

    public static void tick() {
        particles.add(new Particle(Math.random() * 100, Math.random() * 100));
        particles.removeIf(p -> p.life <= 0);

        for (Particle p : particles) {
            p.update();
        }
    }

    public static void render(DrawContext context) {
        for (Particle p : particles) {
            p.draw(context);
        }
    }

    static class Particle {
        double x, y;
        int life = 60;

        Particle(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            y += 0.3;
            life--;
        }

        void draw(DrawContext context) {
            context.drawText(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                "•",
                (int) x,
                (int) y,
                0xFFFFFF,
                false
            );
        }
    }
}
