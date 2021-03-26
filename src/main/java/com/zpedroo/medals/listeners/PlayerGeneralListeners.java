package com.zpedroo.medals.listeners;

import com.zpedroo.medals.Main;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerGeneralListeners implements Listener {

    private Main main;

    public PlayerGeneralListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> main.getDataManager().loadPlayer(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> main.getDataManager().savePlayer(event.getPlayer()), 1L);
        main.getPlayerManager().removeDisplayMedal(event.getPlayer());
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        if (main.getPlayerManager().getDisplayMedal(event.getPlayer()) != null) {
            main.getHologramManager().removeHologram(event.getPlayer());
            main.getHologramManager().createHologram(event.getPlayer(), main.getPlayerManager().getDisplayMedal(event.getPlayer()));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> main.getHologramManager().updateHologram(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        main.getHologramManager().removeHologram(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        main.getServer().getScheduler().runTaskLater(main, () -> main.getHologramManager().createHologram(event.getPlayer(), main.getPlayerManager().getDisplayMedal(event.getPlayer())), 1L);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isAsynchronous() && main.getPlayerManager().getDisplayMedal(event.getPlayer()) != null) {
            if (event.isSneaking()) {
                main.getHologramManager().removeHologram(event.getPlayer());
            }
            else {
                main.getHologramManager().createHologram(event.getPlayer(), main.getPlayerManager().getDisplayMedal(event.getPlayer()));
            }
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (main.getPlayerManager().getDisplayMedal(event.getPlayer()) != null) {
            if (event.getNewGameMode() == GameMode.SPECTATOR) {
                main.getHologramManager().removeHologram(event.getPlayer());
            }
            else {
                main.getHologramManager().createHologram(event.getPlayer(), main.getPlayerManager().getDisplayMedal(event.getPlayer()));
            }
        }
    }
}
