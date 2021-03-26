package com.zpedroo.medals;

import com.comphenix.protocol.ProtocolLibrary;
import com.zpedroo.medals.commands.MedalsCmd;
import com.zpedroo.medals.data.Custom;
import com.zpedroo.medals.data.DataManager;
import com.zpedroo.medals.data.Medal;
import com.zpedroo.medals.data.SQLiteConnector;
import com.zpedroo.medals.listeners.InventoryClickListener;
import com.zpedroo.medals.listeners.PlayerGeneralListeners;
import com.zpedroo.medals.managers.FileManager;
import com.zpedroo.medals.managers.HologramManager;
import com.zpedroo.medals.managers.PlayerManager;
import com.zpedroo.medals.placeholders.Placeholders;
import com.zpedroo.medals.protocol.HologramPacketListener;
import com.zpedroo.medals.utils.ItemBuilder;
import com.zpedroo.medals.utils.MedalsMenu;
import com.zpedroo.medals.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Main extends JavaPlugin {

    private static Main main;
    public static Main get() { return main; }
    private List<Medal> medals;
    private List<Custom> customs;
    private PlayerManager playerManager;
    private HologramManager hologramManager;
    private MedalsMenu medalsMenu;
    private Placeholders placeholders;
    private SQLiteConnector sqLiteConnector;
    private DataManager dataManager;

    private HashMap<String, FileManager> files = new HashMap<>(1);
    public HashMap<String, FileManager> getFiles() { return files; }

    public void onEnable() {
        main = this;
        getFiles().put("CONFIG", new FileManager("", "config", "configuration-files/config"));
        medals = new ArrayList<>();
        customs = new ArrayList<>();
        playerManager = new PlayerManager();
        hologramManager = new HologramManager(this);
        medalsMenu = new MedalsMenu(getFiles().get("CONFIG"));
        placeholders = new Placeholders();
        sqLiteConnector = new SQLiteConnector();
        dataManager = new DataManager();
        registerCommands();
        registerListeners();
        loadMedals();
        loadCustoms();
        VaultHook.setupEconomy();
        ProtocolLibrary.getProtocolManager().addPacketListener(new HologramPacketListener(this));
    }

    public void onDisable() {
        getDataManager().saveAll();
        try {
            getSQLiteConnector().closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getHologramManager().clear();
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }

    private void registerCommands() {
        getCommand("medals").setExecutor(new MedalsCmd());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(this), this);
    }

    private void loadMedals() {
        FileManager file = getFiles().get("CONFIG");
        Set<String> section = file.get().getConfigurationSection("Medals").getKeys(false);
        if (section != null && section.size() >= 1) {
            int id = 0;
            for (String medal : section) {
                if (medal == null) continue;

                String medalDisplayName = file.get().getString("Medals." + medal + ".display").replaceAll("&", "§");
                String medalPermission = file.get().getString("Medals." + medal + ".permission");
                ItemStack item = ItemBuilder.build(file, "Medals." + medal + ".item").build();
                addMedal(new Medal(medal, medalDisplayName, medalPermission, item, ++id));
            }
        }
    }

    private void loadCustoms() {
        FileManager file = getFiles().get("CONFIG");
        Set<String> section = file.get().getConfigurationSection("Customs").getKeys(false);
        if (section != null && section.size() >= 1) {
            int id = 0;
            for (String custom : section) {
                if (custom == null) continue;

                String customExtra = file.get().getString("Customs." + custom + ".extra").replaceAll("&", "§");
                ItemStack item = ItemBuilder.build(file, "Customs." + custom + ".item").build();
                addCustom(new Custom(custom, customExtra, item, ++id));
            }
        }
    }

    public void addMedal(Medal medal) {
        getMedals().add(medal);
    }

    public void addCustom(Custom custom) {
        getCustoms().add(custom);
    }

    public List<Medal> getMedals() {
        return medals;
    }

    public List<Custom> getCustoms() {
        return customs;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public MedalsMenu getMedalsMenu() {
        return medalsMenu;
    }

    public Placeholders getPlaceholders() {
        return placeholders;
    }

    public SQLiteConnector getSQLiteConnector() {
        return sqLiteConnector;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}