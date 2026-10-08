package com.nyretha.tools.model;

import org.bukkit.Material;
import java.util.List;

public class CustomTool {
    private final String id;
    private final Material material;
    private final String displayName;
    private final int slot;
    private final int page;
    private final List<String> lore;

    public CustomTool(String id, Material material, String displayName, int slot, int page, List<String> lore) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.slot = slot;
        this.page = page;
        this.lore = lore;
    }

    public CustomTool(String id, Material material, String displayName, int slot, int page) {
        this(id, material, displayName, slot, page, List.of());
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public int getSlot() { return slot; }
    public int getPage() { return page; }
    public List<String> getLore() { return lore; }
}
