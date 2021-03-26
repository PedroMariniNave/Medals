package com.zpedroo.medals.data;

import com.zpedroo.medals.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerMedal {

    private List<Medal> playerMedals;

    public PlayerMedal(Player player) {
        playerMedals = new ArrayList<>();
        for (Medal medal : Main.get().getMedals()) {
            if (medal == null) continue;

            if (player.hasPermission(medal.getPermission())) {
                playerMedals.add(medal);
            }
        }
    }

    public List<Medal> getPlayerMedals() {
        return playerMedals;
    }
}