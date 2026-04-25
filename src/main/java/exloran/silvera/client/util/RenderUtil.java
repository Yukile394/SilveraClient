package com.silvera.client.util;

import net.minecraft.client.gui.DrawContext;

public class RenderUtil {

    public static void drawRoundedRect(DrawContext ctx,
                                        int x, int y, int w, int h,
                                        int radius, int color) {
        ctx.fill(x + radius, y,            x + w - radius, y + h,            color);
        ctx.fill(x,          y + radius,   x + radius,     y + h - radius,   color);
        ctx.fill(x + w - radius, y + radius, x + w,        y + h - radius,   color);

        fillCorner(ctx, x,             y,             radius, 0, color);
        fillCorner(ctx, x + w - radius, y,             radius, 1, color);
        fillCorner(ctx, x + w - radius, y + h - radius, radius, 2, color);
        fillCorner(ctx, x,             y + h - radius, radius, 3, color);
    }

    private static void fillCorner(DrawContext ctx,
                                    int cx, int cy, int radius,
                                    int corner, int color) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                int dx = radius - 1 - i;
                int dy = radius - 1 - j;
                if (dx * dx + dy * dy <= (radius - 0.5f) * (radius - 0.5f)) {
                    int px, py;
                    switch (corner) {
                        case 0 -> { px = cx + i;             py = cy + j; }
                        case 1 -> { px = cx + (radius-1-i);  py = cy + j; }
                        case 2 -> { px = cx + (radius-1-i);  py = cy + (radius-1-j); }
                        default -> { px = cx + i;            py = cy + (radius-1-j); }
                    }
                    ctx.fill(px, py, px + 1, py + 1, color);
                }
            }
        }
    }
}
