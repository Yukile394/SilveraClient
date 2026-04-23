package com.silvera.gui;

import com.silvera.module.api.Module;
import com.silvera.module.api.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/**
 * SilveraMenuScreen — Silvera Client'ın animasyonlu mod menüsü.
 *
 * Layout:
 *   Sol panel → Kategori listesi
 *   Sağ panel → Seçili kategorinin modülleri (toggle butonları)
 *   Üst başlık → "Silvera Client" animasyonlu gradient
 *   Alt bar    → Mod açıklaması
 */
public class SilveraMenuScreen extends Screen {

    // ── Renkler ───────────────────────────────────────────────────
    private static final int BG_DARK      = 0xE8080810;
    private static final int BG_PANEL     = 0xEE0D0D1F;
    private static final int ACCENT       = 0xFFAA66FF;
    private static final int ACCENT2      = 0xFF66AAFF;
    private static final int TEXT_BRIGHT  = 0xFFFFFFFF;
    private static final int TEXT_DIM     = 0xFFAAAAAA;
    private static final int ENABLED_BG   = 0x9933FF88;
    private static final int DISABLED_BG  = 0x44FFFFFF;
    private static final int HOVER_BG     = 0x55AA66FF;
    private static final int SEP_COLOR    = 0xFF2A2A4A;

    // ── Layout sabitler ──────────────────────────────────────────
    private static final int MENU_W      = 380;
    private static final int MENU_H      = 260;
    private static final int CAT_W       = 100;
    private static final int HEADER_H    = 30;
    private static final int FOOTER_H    = 20;
    private static final int MODULE_H    = 22;
    private static final int CAT_H       = 26;

    // ── State ────────────────────────────────────────────────────
    private Module.Category selectedCategory = Module.Category.PARTICLES;
    private int hoveredModule   = -1;
    private int hoveredCategory = -1;
    private String footerText   = "Silvera Client — Tüm özellikler burada";

    // ── Animasyon ────────────────────────────────────────────────
    private float openAnimation  = 0f;   // 0→1 açılış
    private float titlePulse     = 0f;
    private long openTime;

    public SilveraMenuScreen() {
        super(Text.literal("Silvera Client"));
    }

    @Override
    protected void init() {
        openTime = System.currentTimeMillis();
    }

    // ── Ana render ───────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Animasyon lerp
        float elapsed = (System.currentTimeMillis() - openTime) / 200f;
        openAnimation = Math.min(1f, elapsed);
        float ease = easeOutCubic(openAnimation);
        titlePulse = (float)(System.currentTimeMillis() % 2000) / 2000f;

        int sw = width;
        int sh = height;
        int mx = sw / 2 - MENU_W / 2;
        int my = sh / 2 - MENU_H / 2;

        // Scale efekti: açılışta küçükten büyüğe
        float scale = 0.85f + ease * 0.15f;
        ctx.getMatrices().push();
        ctx.getMatrices().translate(sw / 2f, sh / 2f, 0);
        ctx.getMatrices().scale(scale, scale, 1);
        ctx.getMatrices().translate(-sw / 2f, -sh / 2f, 0);

        // Karartma
        ctx.fill(0, 0, sw, sh, (int)(0x99 * ease) << 24);

        // ── Ana çerçeve ──────────────────────────────────────────
        drawRoundedRect(ctx, mx, my, MENU_W, MENU_H, BG_DARK);

        // ── Header ───────────────────────────────────────────────
        drawHeader(ctx, mx, my, ease);

        // ── Kategori paneli (sol) ─────────────────────────────────
        drawCategoryPanel(ctx, mx, my, mouseX, mouseY, ease);

        // ── Modül paneli (sağ) ───────────────────────────────────
        drawModulePanel(ctx, mx, my, mouseX, mouseY, ease);

        // ── Footer ───────────────────────────────────────────────
        drawFooter(ctx, mx, my, ease);

        // Kenar çizgisi / glow
        drawBorder(ctx, mx, my, ease);

        ctx.getMatrices().pop();

        super.render(ctx, mouseX, mouseY, delta);
    }

    // ── Header çizimi ────────────────────────────────────────────

    private void drawHeader(DrawContext ctx, int mx, int my, float ease) {
        // Gradient header bar
        int hColor1 = lerpColor(0xFF1A1A3A, 0xFF0A0A25, 0.5f);
        ctx.fill(mx, my, mx + MENU_W, my + HEADER_H, hColor1);

        // Divider
        ctx.fill(mx, my + HEADER_H - 1, mx + MENU_W, my + HEADER_H, ACCENT);

        // Başlık metni — pulse efekti
        float pulse = (float)(0.85 + 0.15 * Math.sin(titlePulse * Math.PI * 2));
        String title = "✦ Silvera Client ✦";
        int titleColor = lerpColor(ACCENT, ACCENT2, titlePulse);

        int tw = this.textRenderer.getWidth(title);
        drawCenteredText(ctx, title, mx + MENU_W / 2, my + 11, titleColor);

        // Sürüm
        String ver = "v1.0.0";
        ctx.drawTextWithShadow(this.textRenderer, "§8" + ver,
                mx + MENU_W - this.textRenderer.getWidth(ver) - 6, my + 11, TEXT_DIM);
    }

    // ── Kategori paneli ──────────────────────────────────────────

    private void drawCategoryPanel(DrawContext ctx, int mx, int my,
                                    int mouseX, int mouseY, float ease) {
        int panelX = mx;
        int panelY = my + HEADER_H;
        int panelH = MENU_H - HEADER_H - FOOTER_H;

        ctx.fill(panelX, panelY, panelX + CAT_W, panelY + panelH, BG_PANEL);
        // Sağ divider
        ctx.fill(panelX + CAT_W - 1, panelY, panelX + CAT_W, panelY + panelH, SEP_COLOR);

        Module.Category[] cats = Module.Category.values();
        hoveredCategory = -1;

        for (int i = 0; i < cats.length; i++) {
            Module.Category cat = cats[i];
            int cy = panelY + i * CAT_H + 6;
            int cx = panelX;

            boolean isSelected = cat == selectedCategory;
            boolean isHovered  = mouseX >= cx && mouseX < cx + CAT_W
                               && mouseY >= cy - 2 && mouseY < cy + CAT_H - 2;
            if (isHovered) hoveredCategory = i;

            // Arka plan
            if (isSelected) {
                ctx.fill(cx, cy - 2, cx + CAT_W - 1, cy + CAT_H - 4, 0x55AA66FF);
                // Sol accent bar
                ctx.fill(cx, cy - 2, cx + 2, cy + CAT_H - 4, ACCENT);
            } else if (isHovered) {
                ctx.fill(cx, cy - 2, cx + CAT_W - 1, cy + CAT_H - 4, 0x22FFFFFF);
            }

            int textColor = isSelected ? ACCENT : (isHovered ? TEXT_BRIGHT : TEXT_DIM);
            ctx.drawTextWithShadow(this.textRenderer, cat.label, cx + 8, cy + 3, textColor);
        }
    }

    // ── Modül paneli ─────────────────────────────────────────────

    private void drawModulePanel(DrawContext ctx, int mx, int my,
                                  int mouseX, int mouseY, float ease) {
        int panelX = mx + CAT_W;
        int panelY = my + HEADER_H;
        int panelW = MENU_W - CAT_W;
        int panelH = MENU_H - HEADER_H - FOOTER_H;

        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xDD050510);

        List<Module> modules = ModuleManager.getByCategory(selectedCategory);
        hoveredModule = -1;

        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            int rowY = panelY + i * MODULE_H + 4;
            int rowX = panelX + 4;
            int rowW = panelW - 8;

            boolean hovered = mouseX >= rowX && mouseX < rowX + rowW
                           && mouseY >= rowY && mouseY < rowY + MODULE_H - 2;
            if (hovered) {
                hoveredModule = i;
                footerText = mod.getDescription();
            }

            // Satır arkaplanı
            int rowBg = mod.isEnabled()
                    ? (hovered ? 0xAA33CC77 : ENABLED_BG)
                    : (hovered ? HOVER_BG   : DISABLED_BG);
            drawRoundedRect(ctx, rowX, rowY, rowW, MODULE_H - 3, rowBg);

            // Sol renk şeridi
            if (mod.isEnabled()) {
                ctx.fill(rowX, rowY, rowX + 2, rowY + MODULE_H - 3, 0xFF44FF99);
            }

            // İsim
            String nameColor = mod.isEnabled() ? "§a" : "§7";
            ctx.drawTextWithShadow(this.textRenderer, nameColor + mod.getName(),
                    rowX + 7, rowY + 5, TEXT_BRIGHT);

            // Toggle göstergesi
            String toggleText = mod.isEnabled() ? "§a✔" : "§8✗";
            int ttw = this.textRenderer.getWidth(toggleText);
            ctx.drawTextWithShadow(this.textRenderer, toggleText,
                    rowX + rowW - ttw - 5, rowY + 5, TEXT_BRIGHT);
        }
    }

    // ── Footer ───────────────────────────────────────────────────

    private void drawFooter(DrawContext ctx, int mx, int my, float ease) {
        int fy = my + MENU_H - FOOTER_H;
        ctx.fill(mx, fy, mx + MENU_W, my + MENU_H, 0xEE080815);
        ctx.fill(mx, fy, mx + MENU_W, fy + 1, SEP_COLOR);

        // Kısaltılmış footer metni
        String ft = footerText.length() > 55
                ? footerText.substring(0, 52) + "..." : footerText;
        ctx.drawTextWithShadow(this.textRenderer, "§7" + ft,
                mx + 6, fy + 5, TEXT_DIM);

        // Sağda kısayol ipucu
        String hint = "§8[RShift] Kapat";
        ctx.drawTextWithShadow(this.textRenderer, hint,
                mx + MENU_W - this.textRenderer.getWidth(hint) - 6, fy + 5, TEXT_DIM);
    }

    // ── Tıklama ──────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sw = width;
        int sh = height;
        int mx = sw / 2 - MENU_W / 2;
        int my = sh / 2 - MENU_H / 2;

        // Kategori tıklama
        Module.Category[] cats = Module.Category.values();
        int panelY = my + HEADER_H;
        for (int i = 0; i < cats.length; i++) {
            int cy = panelY + i * CAT_H + 6;
            if (mouseX >= mx && mouseX < mx + CAT_W
                    && mouseY >= cy - 2 && mouseY < cy + CAT_H - 2) {
                selectedCategory = cats[i];
                footerText = "Silvera Client — " + cats[i].label;
                return true;
            }
        }

        // Modül tıklama
        List<Module> modules = ModuleManager.getByCategory(selectedCategory);
        int modPanelX = mx + CAT_W;
        for (int i = 0; i < modules.size(); i++) {
            int rowY = panelY + i * MODULE_H + 4;
            int rowX = modPanelX + 4;
            int rowW = MENU_W - CAT_W - 8;

            if (mouseX >= rowX && mouseX < rowX + rowW
                    && mouseY >= rowY && mouseY < rowY + MODULE_H - 2) {
                modules.get(i).toggle();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ── Yardımcı çizim metodları ─────────────────────────────────

    private void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x + 1, y, x + w - 1, y + h, color);
        ctx.fill(x, y + 1, x + w, y + h - 1, color);
    }

    private void drawBorder(DrawContext ctx, int mx, int my, float ease) {
        int alpha = (int)(0xAA * ease);
        int col = (alpha << 24) | (ACCENT & 0x00FFFFFF);
        // Üst
        ctx.fill(mx, my, mx + MENU_W, my + 1, col);
        // Alt
        ctx.fill(mx, my + MENU_H - 1, mx + MENU_W, my + MENU_H, col);
        // Sol
        ctx.fill(mx, my, mx + 1, my + MENU_H, col);
        // Sağ
        ctx.fill(mx + MENU_W - 1, my, mx + MENU_W, my + MENU_H, col);
    }

    private void drawCenteredText(DrawContext ctx, String text, int cx, int y, int color) {
        int tw = this.textRenderer.getWidth(text);
        ctx.drawTextWithShadow(this.textRenderer, text, cx - tw / 2, y, color);
    }

    private int lerpColor(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int rr = (int)(ar + (br - ar) * t);
        int rg = (int)(ag + (bg - ag) * t);
        int rb = (int)(ab + (bb - ab) * t);
        return 0xFF000000 | (rr << 16) | (rg << 8) | rb;
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    @Override
    public boolean shouldPause() { return false; }
}
