package com.zpedroo.medals.utils;

import com.skydhs.voltzspawners.utils.nbti.NBTItem;
import com.zpedroo.medals.Main;
import com.zpedroo.medals.data.PlayerMedal;
import com.zpedroo.medals.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class MedalsMenu {

    private FileManager file;
    private int MAIN_ROWS;
    private int CUSTOM_ROWS;
    private String MAIN_TITLE;
    private String CUSTOM_TITLE;

    public MedalsMenu(FileManager file){
        this.file = file;
        this.MAIN_ROWS = file.get().getInt("Settings.main-inventory-rows");
        this.CUSTOM_ROWS = file.get().getInt("Settings.custom-inventory-rows");
        this.MAIN_TITLE = file.get().getString("Settings.main-inventory-name");
        this.CUSTOM_TITLE = file.get().getString("Settings.custom-inventory-name");
    }

    public void openMedalsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*MAIN_ROWS, MAIN_TITLE);
        PlayerMedal playerMedal = new PlayerMedal(player);

        if (playerMedal.getPlayerMedals().size() == 0) {
            inventory.setItem(file.get().getInt("NoMedals.slot"), ItemBuilder.build(file, "NoMedals").build());
        }
        else {
            int slot = 10;
            for (int i = 0; i < playerMedal.getPlayerMedals().size(); ++i) {
                if (slot == 15 || slot == 24 || slot == 33) slot += 4;
                ItemStack item = playerMedal.getPlayerMedals().get(i).getItem().clone();
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName()) meta.setDisplayName(Main.get().getPlaceholders().replacePlaceholders(player, meta.getDisplayName()));
                ArrayList<String> lore;
                if (meta.hasLore()) {
                    lore = (ArrayList<String>) meta.getLore();
                } else {
                    lore = new ArrayList<>();
                }
                boolean using = false;
                if (Main.get().getPlayerManager().getDisplayMedal(player) != null
                        && Main.get().getPlayerManager().getDisplayMedal(player).equals(playerMedal.getPlayerMedals().get(i))){
                    using = true;
                    meta.addEnchant(Enchantment.OXYGEN, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                for (String str : file.get().getStringList("MedalLore." + (using ? "using" : "no-using"))) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', str));
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
                NBTItem nbt = new NBTItem(item);
                nbt.setInteger("MedalID", i);
                inventory.setItem(++slot, nbt.getItem());
            }

            inventory.setItem(file.get().getInt("RemoveMedal.slot"), ItemBuilder.build(file, "RemoveMedal").build());
            inventory.setItem(file.get().getInt("ModifyCustom.slot"), ItemBuilder.build(file, "ModifyCustom").build());
        }
        player.openInventory(inventory);
    }

    public void openCustomsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9*CUSTOM_ROWS, CUSTOM_TITLE);
        int slot = 10;
        for (int i = 0; i < Main.get().getCustoms().size(); ++i) {
            if (slot == 15 || slot == 24 || slot == 33) slot += 4;
            ItemStack item = Main.get().getCustoms().get(i).getItem().clone();
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) meta.setDisplayName(Main.get().getPlaceholders().replacePlaceholders(player, meta.getDisplayName()));
            ArrayList<String> lore;
            if (meta.hasLore()) {
                lore = (ArrayList<String>) meta.getLore();
            } else {
                lore = new ArrayList<>();
            }
            boolean using = false;
            if (Main.get().getPlayerManager().getCustom(player) != null
                    && Main.get().getPlayerManager().getCustom(player).equals(Main.get().getCustoms().get(i))){
                using = true;
                meta.addEnchant(Enchantment.OXYGEN, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            for (String str : file.get().getStringList("CustomLore." + (using ? "using" : "no-using"))) {
                lore.add(ChatColor.translateAlternateColorCodes('&', str));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            NBTItem nbt = new NBTItem(item);
            nbt.setInteger("CustomID", i);
            inventory.setItem(++slot, nbt.getItem());
        }

        inventory.setItem(file.get().getInt("BackItem.slot"), ItemBuilder.build(file, "BackItem").build());
        player.openInventory(inventory);
    }
}