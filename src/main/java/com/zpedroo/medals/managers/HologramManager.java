package com.zpedroo.medals.managers;

import com.comphenix.protocol.ProtocolLibrary;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.zpedroo.medals.Main;
import com.zpedroo.medals.data.Medal;
import com.zpedroo.medals.data.PlayerMedal;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HologramManager {

    private Main main;
    private Map<UUID, Hologram> holoMap;
    private HologramTask hologramTask;
    private boolean showSelfOnStaying;
    private StayingCheckerTask stayingCheckerTask;


    public HologramManager(Main main) {
        this.holoMap = new HashMap<>();
        this.main = main;
        this.showSelfOnStaying = main.getFiles().get("CONFIG").get().getBoolean("Settings.show-self-on-stay");
        if (showSelfOnStaying) {
            (this.stayingCheckerTask = new StayingCheckerTask()).runTaskTimer(main, 0L, 30L);
        }
        (this.hologramTask = new HologramTask()).runTaskTimerAsynchronously(main, 0L, Main.get().getFiles().get("CONFIG").get().getInt("Settings.update-interval"));
    }

    public void clear() {
        if (getStayingCheckerTask() != null) getStayingCheckerTask().cancel();
        if (getHologramTask() != null) getHologramTask().cancel();

        for (Hologram hologram : holoMap.values()) {
            hologram.delete();
        }
        holoMap.clear();
    }

    private void appendDisplayText(Player player, Hologram hologram, Medal medal) {
        hologram.appendTextLine(Main.get().getPlaceholders().replacePlaceholders(player, medal.getDisplayName()));
    }

    public void createHologram(Player player, Medal medal) {
        if (holoMap.containsKey(player.getUniqueId())) return;
        if (medal == null) return;

        Hologram hologram = HologramsAPI.createHologram(main, getHeadLocation(player));
        appendDisplayText(player, hologram, medal);
        if (!showSelfOnStaying) {
            hologram.getVisibilityManager().hideTo(player);
        }
        updateTrackers(player, hologram);
        holoMap.put(player.getUniqueId(), hologram);
    }

    private void updateTrackers(Player player, Hologram hologram) {
        VisibilityManager visibilityManager = hologram.getVisibilityManager();
        List<Player> entityTrackers = ProtocolLibrary.getProtocolManager().getEntityTrackers(player);
        for (Player target : player.getWorld().getPlayers()) {
            if (target.equals(player)) {
                if (showSelfOnStaying) continue;

                visibilityManager.hideTo(player);
            }

            if (entityTrackers.contains(target)) {
                visibilityManager.showTo(target);
            }
            else {
                visibilityManager.hideTo(target);
            }
        }
    }

    public void removeHologram(Player player) {
        main.getServer().getScheduler().runTask(main, () -> {
            if (holoMap.containsKey(player.getUniqueId())) {
                holoMap.get(player.getUniqueId()).delete();
                holoMap.remove(player.getUniqueId());
            }
        });
    }

    public void updateHologram(Player player) {
        if (player.hasMetadata("vanished")) {
            Main.get().getPlayerManager().removeDisplayMedal(player);
            return;
        }
        List<Medal> medals = new PlayerMedal(player).getPlayerMedals();
        if (medals.isEmpty() || isHidden(player)) {
            if (hasHologram(player)) {
                main.getServer().getScheduler().runTask(main, () -> removeHologram(player));
            }
            return;
        }
        if (hasHologram(player)) {
            Hologram hologram = holoMap.get(player.getUniqueId());
            hologram.teleport(getHeadLocation(player));
            updateTrackers(player, hologram);
            if (getStayingCheckerTask() != null) {
                getStayingCheckerTask().checkLocation(player);
            }
        }
        else {
            main.getServer().getScheduler().runTask(main, () -> createHologram(player, main.getPlayerManager().getDisplayMedal(player)));
        }
    }

    public boolean hasHologram(Player player) {
        return holoMap.containsKey(player.getUniqueId());
    }

    public StayingCheckerTask getStayingCheckerTask() {
        return stayingCheckerTask;
    }

    public HologramTask getHologramTask() {
        return hologramTask;
    }

    private Location getHeadLocation(Player player) {
        return player.getLocation().add(0.0, 2.58, 0.0);
    }

    private boolean isHidden(Player player) {
        return player.isDead() || player.isSneaking() || player.getGameMode() == GameMode.SPECTATOR || player.hasPotionEffect(PotionEffectType.INVISIBILITY) || player.getPassenger() != null || player.getVehicle() != null;
    }

    public class HologramTask extends BukkitRunnable {

        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uniqueId = player.getUniqueId();
                if (holoMap.containsKey(uniqueId)) {
                    updateHologram(player);
                }
            }
        }
    }

    public class StayingCheckerTask extends BukkitRunnable {

        private Map<UUID, Location> locMap;

        public StayingCheckerTask() {
            this.locMap = new HashMap<>();
        }

        public void run() {
            updateAllTrackers();
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uniqueId = player.getUniqueId();
                if (holoMap.containsKey(uniqueId) && locMap.containsKey(uniqueId) && locMap.get(uniqueId).getWorld().equals(player.getLocation().getWorld())) {
                    List<Medal> medals = new PlayerMedal(player).getPlayerMedals();
                    if (isHidden(player) || (medals.isEmpty() && holoMap.containsKey(uniqueId)) || locMap.get(uniqueId).distance(player.getLocation()) > 0.2) {
                        hide(player);
                    }
                    else {
                        if (medals.isEmpty()) continue;

                        show(player);
                    }
                }
            }
            updateOldLocations();
        }

        private void updateAllTrackers() {
            for (UUID uuid : holoMap.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (!isHidden(player)) {
                    updateTrackers(player, holoMap.get(player.getUniqueId()));
                }
            }
        }

        private void updateOldLocations() {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                UUID uniqueId = player.getUniqueId();
                if (holoMap.containsKey(uniqueId)) {
                    locMap.put(uniqueId, player.getLocation());
                }
            }
        }

        public void checkLocation(Player player) {
            UUID uniqueId = player.getUniqueId();
            if (locMap.containsKey(uniqueId)) {
                Location location = locMap.get(uniqueId);
                if (location.getWorld().equals(player.getWorld()) && location.distance(player.getLocation()) > 0.2) {
                    hide(player);
                }
            }
        }

        public void show(Player player) {
            createHologram(player, main.getPlayerManager().getDisplayMedal(player));
            Hologram hologram = holoMap.get(player.getUniqueId());
            hologram.teleport(getHeadLocation(player));
            hologram.getVisibilityManager().showTo(player);
        }

        public void hide(Player player) {
            if (!holoMap.containsKey(player.getUniqueId())) return;

            holoMap.get(player.getUniqueId()).getVisibilityManager().hideTo(player);
        }
    }
}
