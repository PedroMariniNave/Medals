package com.zpedroo.medals.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.zpedroo.medals.managers.FileManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(ItemStack item) {
        Validate.notNull(item);
        this.item = item;
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        Validate.notNull(material);
        if (material.toString().equalsIgnoreCase("SKULL_ITEM")) {
            item = new ItemStack(material, amount, (short) 3);
            return;
        }

        item = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, short durability) {
        Validate.notNull(material);
        if (material.toString().equalsIgnoreCase("SKULL_ITEM") && (durability != 3)) {
            item = new ItemStack(material, amount, (short) 3);
            return;
        }

        item = new ItemStack(material, amount, durability);
    }

    public ItemBuilder withName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        if (lore == null || lore.size() <= 0) return this;

        List<String> toAdd = new ArrayList<>(lore.size());

        for (String str : lore) {
            toAdd.add(ChatColor.translateAlternateColorCodes('&', str));
        }

        meta.setLore(toAdd);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withLore(List<String> lore, String[] placeholders, String[] replacers) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        if (lore == null || lore.size() <= 0) return this;

        List<String> toAdd = new ArrayList<>(lore.size());
        boolean replace = placeholders != null && placeholders.length > 0 && placeholders.length == replacers.length;

        for (String str : lore) {
            toAdd.add(ChatColor.translateAlternateColorCodes('&', replace ? StringUtils.replaceEach(str, placeholders, replacers) : str));
        }

        meta.setLore(toAdd);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withLore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        if (lore == null) return this;

        List<String> toAdd = new ArrayList<>(lore.length + 1);

        for (String str : lore) {
            toAdd.add(ChatColor.translateAlternateColorCodes('&', str));
        }

        meta.setLore(toAdd);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore(String line) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        if (!meta.hasLore()) return this;

        List<String> toAdd = new ArrayList<>(meta.getLore());

        for (String str : meta.getLore()) {
            if (StringUtils.equalsIgnoreCase(str, line)) {
                toAdd.remove(str);
                break;
            }
        }

        meta.setLore(toAdd);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore(int index) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        if (!meta.hasLore()) return this;

        List<String> toAdd = new ArrayList<>(meta.getLore());
        if (toAdd.size() < index) return this;
        toAdd.remove(index);

        meta.setLore(toAdd);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withEnchantment(Enchantment enchant, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;

        meta.addEnchant(enchant, level, true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder withEnchantment(Enchantment enchant) {
        return withEnchantment(enchant, 1);
    }

    public ItemBuilder setGlow() {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;

        meta.addEnchant(Enchantment.OXYGEN, 1, true);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        if (!item.getType().equals(Material.SKULL_ITEM)) return this;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return this;

        meta.setOwner(owner);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setCustomTexture(String url) {
        Validate.notNull(url);
        if (!item.getType().equals(Material.SKULL_ITEM)) return this;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return this;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return item;
    }

    public static ItemBuilder build(FileManager file, String where) {
        return build(file, where, null, null);
    }

    public static ItemBuilder build(FileManager file, String where, String[] placeholders, String[] replacers) {
        ItemBuilder item = null;

        try {
            String type = StringUtils.replace(file.get().getString(where + ".type"), " ", "").toUpperCase();
            int amount = file.get().contains(where + ".amount") ? file.get().getInt(where + ".amount") : 1;

            if (StringUtils.contains(type, ":")) {
                String[] typeSplit = type.split(":");
                short durability = Short.parseShort(typeSplit[1]);
                item = new ItemBuilder(Material.getMaterial(typeSplit[0]), amount, durability);
            } else {
                item = new ItemBuilder(Material.getMaterial(type), amount);
            }

            if (file.get().contains(where + ".owner")) {
                String owner = file.get().getString(where + ".owner");

                if (owner.length() <= 17) {
                    item.setSkullOwner(owner);
                } else {
                    item.setCustomTexture(owner);
                }
            }

            if (file.get().contains(where + ".name")) {
                String name = ChatColor.translateAlternateColorCodes('&', file.get().getString(where + ".name"));

                if (placeholders != null && placeholders.length > 0 && placeholders.length == replacers.length) {
                    name = StringUtils.replaceEach(name, placeholders, replacers);
                }

                item.withName(name);
            }

            if (file.get().contains(where + ".lore")) {
                item.withLore(file.get().getStringList(where + ".lore"), placeholders, replacers);
            }

            if (file.get().contains(where + ".glow") && file.get().getString(where + ".glow").equalsIgnoreCase("true")) {
                item.setGlow();
            } else {
                if (file.get().contains(where + ".enchants")) {
                    for (String str : file.get().getStringList(where + ".enchants")) {
                        String enchantment = StringUtils.replace(str, " ", "");

                        try {
                            if (StringUtils.contains(enchantment, ",")) {
                                String[] enchantmentSplit = enchantment.split(",");
                                item.withEnchantment(Enchantment.getByName(enchantmentSplit[0]), Integer.parseInt(enchantmentSplit[1]));
                            } else {
                                item.withEnchantment(Enchantment.getByName(enchantment));
                            }
                        } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                        }
                    }
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        return item;
    }
}