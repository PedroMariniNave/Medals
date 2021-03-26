package com.zpedroo.medals.data;

import com.zpedroo.medals.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DataManager{

    public HashMap<Player, PlayerData> playerDataCache;

    public DataManager() {
        this.playerDataCache = new HashMap<>();
    }

    public PlayerData loadPlayer(Player player) {
        if (playerDataCache.containsKey(player)) {
            return playerDataCache.get(player);
        }
        PlayerData playerData = Main.get().getSQLiteConnector().loadPlayer(player.getUniqueId());
        playerDataCache.put(player, playerData);
        if (playerData.getCustom() != -1) {
            Main.get().getPlayerManager().setCustom(player, Main.get().getCustoms().get(playerData.getCustom() - 1));
        }

        if (playerData.getID() != -1) {
            Main.get().getPlayerManager().setDisplayMedal(player, Main.get().getMedals().get(playerData.getID() - 1));
        }
        return playerData;
    }

    public boolean savePlayer(Player player) {
        if (playerDataCache.containsKey(player)) {
            PlayerData playerData = playerDataCache.get(player);
            boolean s = Main.get().getSQLiteConnector().savePlayer(playerData);
            if (s) {
                playerDataCache.remove(player);
            }
            return s;
        }
        return false;
    }

    public void saveAll() {
        for (PlayerData playerData : playerDataCache.values()) {
            try {
                Main.get().getSQLiteConnector().savePlayer(playerData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
