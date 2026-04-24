package exloran.silvera.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderer {

    public static void register() {

        HudRenderCallback.EVENT.register(new HudRenderCallback() {
            @Override
            public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
                ParticleHud.tick();
                ParticleHud.render(context);
            }
        });

        System.out.println("HUD Registered");
    }
}
