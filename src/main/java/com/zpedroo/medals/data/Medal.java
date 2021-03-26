package com.zpedroo.medals.data;

import org.bukkit.inventory.ItemStack;

public class Medal {

    private String medalName;
    private String medalDisplayName;
    private String medalPermission;
    private ItemStack medalItem;
    private int id;

    public Medal(String medalName, String medalDisplayName, String medalPermission, ItemStack medalItem, int id) {
        this.medalName = medalName;
        this.medalDisplayName = medalDisplayName;
        this.medalPermission = medalPermission;
        this.medalItem = medalItem;
        this.id = id;
    }

    public String getName() {
        return medalName;
    }

    public String getDisplayName() {
        return medalDisplayName;
    }

    public String getPermission() {
        return medalPermission;
    }

    public ItemStack getItem() {
        return medalItem;
    }

    public int getID() {
        return id;
    }
}