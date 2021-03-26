package com.zpedroo.medals.managers;

import com.zpedroo.medals.Main;
import com.zpedroo.medals.data.Custom;
import com.zpedroo.medals.data.Medal;
import com.zpedroo.medals.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private Map<Player, Medal> playerMedalCache;
    private Map<Player, Custom> playerCustomCache;

    public PlayerManager(){
        playerMedalCache = new HashMap<>();
        playerCustomCache = new HashMap<>();
    }

    public void setDisplayMedal(Player player, Medal medal) {
        if (getDisplayMedal(player) != null) removeDisplayMedal(player);

        PlayerData data = Main.get().getDataManager().loadPlayer(player);
        data.setID(medal.getID());
        playerMedalCache.put(player, medal);
        Main.get().getHologramManager().createHologram(player, medal);
    }

    public void removeDisplayMedal(Player player) {
        Main.get().getHologramManager().removeHologram(player);
        PlayerData data = Main.get().getDataManager().loadPlayer(player);
        data.setID(-1);
        playerMedalCache.remove(player);
    }

    public void setCustom(Player player, Custom custom) {
        playerCustomCache.put(player, custom);
    }

    public Medal getDisplayMedal(Player player) {
        return playerMedalCache.getOrDefault(player, null);
    }

    public Custom getCustom(Player player) {
        return playerCustomCache.get(player);
    }

    public void removeCustom(Player player) {
        playerCustomCache.remove(player);
    }

    public boolean hasCustom(Player player) {
        return playerCustomCache.containsKey(player);
    }
}