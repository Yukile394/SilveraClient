package com.silvera.client.config;

public class SilveraConfig {
    // Mevcut ayarlar
    public static boolean hudEnabled = true;
    public static boolean keystrokesEnabled = true;
    public static boolean cpsEnabled = true;
    public static boolean arrayListEnabled = true;
    public static int red = 255;
    public static int green = 255;
    public static int blue = 255;
    public static int hudX = 5;
    public static int hudY = 5;
    public static boolean fpsEnabled = true;
    public static boolean pingEnabled = false;
    public static String watermark = "Silvera Client";

    // Yeni Özellikler (Aura & Elytra)
    public static boolean auraEnabled = false;
    public static boolean elytraTargetEnabled = false;

    public static void load() {
        System.out.println("Silvera Config Loaded");
    }

    public static void save() {
        System.out.println("Silvera Config Saved");
    }
}

