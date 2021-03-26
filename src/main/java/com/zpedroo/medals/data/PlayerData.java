package com.zpedroo.medals.data;

import java.util.UUID;

public class PlayerData{

    private UUID uuid;
    private int id;
    private int custom;

    public PlayerData(UUID uuid, int id, int custom) {
        this.uuid = uuid;
        this.id = id;
        this.custom = custom;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getCustom() {
        return custom;
    }

    public void setCustom(int custom) {
        this.custom = custom;
    }
}