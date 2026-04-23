package com.silvera.client.config;

public class SilveraConfig {

    // HUD Açık / Kapalı
    public static boolean hudEnabled = true;

    // Keystrokes
    public static boolean keystrokesEnabled = true;

    // CPS Counter
    public static boolean cpsEnabled = true;

    // ArrayList
    public static boolean arrayListEnabled = true;

    // Renk
    public static int red = 255;
    public static int green = 255;
    public static int blue = 255;

    // HUD Pozisyon
    public static int hudX = 5;
    public static int hudY = 5;

    // FPS Göster
    public static boolean fpsEnabled = true;

    // Ping Göster
    public static boolean pingEnabled = false;

    // Watermark Text
    public static String watermark = "Silvera Client";

    public static void load() {
        System.out.println("Silvera Config Loaded");
    }

    public static void save() {
        System.out.println("Silvera Config Saved");
    }
}
