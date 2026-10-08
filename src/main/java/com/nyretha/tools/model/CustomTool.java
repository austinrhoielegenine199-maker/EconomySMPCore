package com.nyretha.tools.model;

import org.bukkit.Material;
import java.util.List;

public class CustomTool {
    private final String id;
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final int slot;

    public CustomTool(String id, Material material, String name, List<String> lore, int slot) {
        this.id = id;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.slot = slot;
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }
    public int getSlot() { return slot; }
}
