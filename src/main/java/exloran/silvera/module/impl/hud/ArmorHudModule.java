package com.silvera.module.impl.hud;

import com.silvera.module.api.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Armor HUD — 4 zırh parçasını durability rengiyle gösterir.
 */
public class ArmorHudModule extends Module {

    public ArmorHudModule() {
        super("Armor HUD", "Zırh parçalarını ve durability'sini gösterir.", Category.HUD);
    }

    public void render(DrawContext ctx, int screenW, int screenH) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        EquipmentSlot[] slots = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST,
            EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        int startX = 4;
        int startY = screenH - 80;

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(slots[i]);
            if (stack.isEmpty() || stack.getItem() == Items.AIR) continue;

            // Item icon
            ctx.drawItem(stack, startX, startY + i * 18);

            // Durability
            if (stack.isDamageable()) {
                int maxDmg = stack.getMaxDamage();
                int curDmg = stack.getDamage();
                float pct = 1.0f - (float) curDmg / maxDmg;

                String color;
                if      (pct > 0.6f) color = "§a";
                else if (pct > 0.3f) color = "§e";
                else                 color = "§c";

                String label = color + (int)(pct * 100) + "%";
                ctx.drawTextWithShadow(mc.textRenderer, label, startX + 18, startY + i * 18 + 4, 0xFFFFFF);
            }
        }
    }
}
