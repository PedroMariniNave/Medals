package com.zpedroo.medals.data;

import org.bukkit.inventory.ItemStack;

public class Custom {

    private String customName;
    private String customExtra;
    private ItemStack item;
    private int id;

    public Custom(String customName, String customExtra, ItemStack item, int id) {
        this.customName = customName;
        this.customExtra = customExtra;
        this.item = item;
        this.id = id;
    }

    public String getCustomName() {
        return customName;
    }

    public String getCustomExtra() {
        return customExtra;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getID() {
        return id;
    }
}
