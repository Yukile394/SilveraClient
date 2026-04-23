package com.silvera.module.api;

/**
 * Silvera Client — Module Base
 * Her özellik bu sınıftan türer.
 */
public abstract class Module {

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    public void onEnable()  {}
    public void onDisable() {}
    public void onTick()    {}

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public String  getName()        { return name; }
    public String  getDescription() { return description; }
    public Category getCategory()   { return category; }
    public boolean isEnabled()      { return enabled; }
    public void    setEnabled(boolean v) { this.enabled = v; if (v) onEnable(); else onDisable(); }

    public enum Category {
        PARTICLES("✦ Particles"),
        HUD("◈ HUD"),
        VISUAL("◉ Visual"),
        COSMETIC("❋ Cosmetic");

        public final String label;
        Category(String label) { this.label = label; }
    }
}
