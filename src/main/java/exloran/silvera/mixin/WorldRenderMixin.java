package exloran.silvera.mixin;

import com.silvera.client.effect.HitEffect;
import com.silvera.client.effect.TrailEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.Camera;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(RenderTickCounter tickCounter, boolean renderBlockOutline,
                          Camera camera, GameRenderer gameRenderer,
                          Matrix4f positionMatrix, Matrix4f projectionMatrix,
                          CallbackInfo ci) {

        HitEffect.tick();
        TrailEffect.tick();

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrices = new MatrixStack();
        matrices.push();

        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;

        matrices.translate(-camX, -camY, -camZ);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();

        // Trail render
        for (TrailEffect.TrailParticle trail : TrailEffect.getTrails()) {
            float alpha = trail.getAlpha();
            BufferBuilder buffer = tessellator.begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR
            );
            float s = 0.15f;
            buffer.vertex(matrices.peek().getPositionMatrix(),
                (float)(trail.x - s), (float)trail.y, (float)(trail.z - s))
                .color(1.0f, 0.1f, 0.1f, alpha * 0.8f);
            buffer.vertex(matrices.peek().getPositionMatrix(),
                (float)(trail.x + s), (float)trail.y, (float)(trail.z - s))
                .color(1.0f, 0.1f, 0.1f, alpha * 0.8f);
            buffer.vertex(matrices.peek().getPositionMatrix(),
                (float)(trail.x + s), (float)trail.y, (float)(trail.z + s))
                .color(1.0f, 0.1f, 0.1f, alpha * 0.4f);
            buffer.vertex(matrices.peek().getPositionMatrix(),
                (float)(trail.x - s), (float)trail.y, (float)(trail.z + s))
                .color(1.0f, 0.1f, 0.1f, alpha * 0.4f);
            net.minecraft.client.render.BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        // Hit dairesi render
        for (HitEffect.HitCircle circle : HitEffect.circles) {
            float alpha = circle.getAlpha();
            float radius = circle.radius;
            int segments = 32;

            BufferBuilder buffer = tessellator.begin(
                VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR
            );

            for (int i = 0; i <= segments; i++) {
                double angle = (2 * Math.PI * i / segments) + Math.toRadians(circle.angle);
                float px = (float)(circle.x + Math.cos(angle) * radius);
                float py = (float)(circle.y);
                float pz = (float)(circle.z + Math.sin(angle) * radius);
                buffer.vertex(matrices.peek().getPositionMatrix(), px, py, pz)
                    .color(1.0f, 0.15f, 0.15f, alpha);
            }

            net.minecraft.client.render.BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrices.pop();
    }
}
