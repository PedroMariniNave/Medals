package com.zpedroo.medals.listeners;

import com.skydhs.voltzspawners.utils.nbti.NBTItem;
import com.zpedroo.medals.Main;
import com.zpedroo.medals.data.Custom;
import com.zpedroo.medals.data.PlayerData;
import com.zpedroo.medals.data.PlayerMedal;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        if (event.getClickedInventory().getName().equals(Main.get().getFiles().get("CONFIG").get().getString("Settings.main-inventory-name"))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            NBTItem nbt = new NBTItem(item);
            if (nbt.hasNBTData() && nbt.hasKey("MedalID")) {
                PlayerMedal playerMedal = new PlayerMedal(player);
                int medalID = nbt.getInteger("MedalID");
                if (Main.get().getPlayerManager().getDisplayMedal(player) != null
                        && Main.get().getPlayerManager().getDisplayMedal(player).equals(playerMedal.getPlayerMedals().get(medalID))) {

                    Main.get().getPlayerManager().removeDisplayMedal(player);
                    Main.get().getMedalsMenu().openMedalsInventory(player);
                    player.playSound(player.getLocation(), Sound.EXPLODE, 4, 4);
                    return;
                }

                if (Main.get().getPlayerManager().getDisplayMedal(player) != null) Main.get().getPlayerManager().removeDisplayMedal(player);
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Main.get().getPlayerManager().setDisplayMedal(player, playerMedal.getPlayerMedals().get(medalID));
                        Main.get().getMedalsMenu().openMedalsInventory(player);
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 4, 4);
                    }
                }.runTaskLater(Main.get(), 1L);
            }

            if (event.getSlot() == Main.get().getFiles().get("CONFIG").get().getInt("RemoveMedal.slot")) {
                Main.get().getPlayerManager().removeDisplayMedal(player);
                Main.get().getMedalsMenu().openMedalsInventory(player);
                player.playSound(player.getLocation(), Sound.EXPLODE, 4, 4);
            }

            if (event.getSlot() == Main.get().getFiles().get("CONFIG").get().getInt("ModifyCustom.slot")) {
                Main.get().getMedalsMenu().openCustomsInventory(player);
                player.playSound(player.getLocation(), Sound.CLICK, 3, 3);
            }
        }

        if (event.getClickedInventory().getName().equals(Main.get().getFiles().get("CONFIG").get().getString("Settings.custom-inventory-name"))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            NBTItem nbt = new NBTItem(item);
            if (nbt.hasNBTData() && nbt.hasKey("CustomID")) {
                PlayerData data = Main.get().getDataManager().loadPlayer(player);
                Custom custom = Main.get().getCustoms().get(nbt.getInteger("CustomID"));
                if (Main.get().getPlayerManager().getCustom(player) != null
                        && Main.get().getPlayerManager().getCustom(player).equals(Main.get().getCustoms().get(nbt.getInteger("CustomID")))) {

                    Main.get().getPlayerManager().removeCustom(player);
                    data.setCustom(-1);
                    Main.get().getMedalsMenu().openCustomsInventory(player);
                    player.playSound(player.getLocation(), Sound.EXPLODE, 4, 4);
                } else {
                    Main.get().getPlayerManager().setCustom(player, custom);
                    data.setCustom(custom.getID());
                    Main.get().getMedalsMenu().openCustomsInventory(player);
                    player.playSound(player.getLocation(), Sound.NOTE_PIANO, 4, 4);
                }
            }

            if (event.getSlot() == Main.get().getFiles().get("CONFIG").get().getInt("BackItem.slot")) {
                Main.get().getMedalsMenu().openMedalsInventory(player);
                player.playSound(player.getLocation(), Sound.CLICK, 3, 3);
            }
        }
    }
}