package com.silvera.client.util;

import com.silvera.client.util.RenderUtil;

public class RenderUtil {

    /**
     * Yuvarlatılmış köşeli dikdörtgen
     * radius = 3 veya 4 güzel görünür
     */
    public static void drawRoundedRect(DrawContext ctx,
                                        int x, int y, int w, int h,
                                        int radius, int color) {
        // Orta alan
        ctx.fill(x + radius, y,          x + w - radius, y + h,          color);
        // Sol şerit
        ctx.fill(x,          y + radius, x + radius,     y + h - radius, color);
        // Sağ şerit
        ctx.fill(x + w - radius, y + radius, x + w,     y + h - radius, color);

        // 4 köşe (çeyrek daire piksel bazlı)
        fillCorner(ctx, x,             y,             radius, 180, color); // sol üst
        fillCorner(ctx, x + w - radius, y,             radius, 270, color); // sağ üst
        fillCorner(ctx, x + w - radius, y + h - radius, radius, 0,  color); // sağ alt
        fillCorner(ctx, x,             y + h - radius, radius, 90,  color); // sol alt
    }

    /**
     * Gradient yuvarlatılmış dikdörtgen (soldan sağa)
     */
    public static void drawRoundedRectGradient(DrawContext ctx,
                                                int x, int y, int w, int h,
                                                int radius,
                                                int colorLeft, int colorRight) {
        // Gradient fill yok DrawContext'te direkt,
        // sol yarı + sağ yarı trick:
        int mid = x + w / 2;
        drawRoundedRect(ctx, x,   y, w / 2 + radius, h, radius, colorLeft);
        drawRoundedRect(ctx, mid, y, w / 2,           h, radius, colorRight);
    }

    // Çeyrek daire piksel bazlı (Bresenham circle)
    private static void fillCorner(DrawContext ctx,
                                    int cx, int cy, int radius,
                                    int startAngle, int color) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                // Köşeye olan mesafe
                int dx = radius - 1 - i;
                int dy = radius - 1 - j;
                if (dx * dx + dy * dy <= (radius - 0.5f) * (radius - 0.5f)) {
                    int px, py;
                    switch (startAngle) {
                        case 180 -> { px = cx + i; py = cy + j; }       // sol üst
                        case 270 -> { px = cx + i; py = cy + j; }       // sağ üst (mirror x)
                        case 0   -> { px = cx + i; py = cy + j; }       // sağ alt
                        default  -> { px = cx + i; py = cy + j; }       // sol alt
                    }
                    // Açıya göre doğru koordinat hesapla
                    switch (startAngle) {
                        case 180 -> ctx.fill(cx + i,           cy + j,           cx + i + 1, cy + j + 1, color);
                        case 270 -> ctx.fill(cx + (radius-1-i), cy + j,           cx + (radius-i), cy + j + 1, color);
                        case 0   -> ctx.fill(cx + (radius-1-i), cy + (radius-1-j), cx + (radius-i), cy + (radius-j), color);
                        case 90  -> ctx.fill(cx + i,           cy + (radius-1-j), cx + i + 1, cy + (radius-j), color);
                    }
                }
            }
        }
    }
}
