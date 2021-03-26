package com.zpedroo.medals.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy;

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @SuppressWarnings("deprecation")
    public static double getBalance(OfflinePlayer off) {
        try {
            return economy.getBalance(off);
        } catch (Throwable e1) {
            try {
                return economy.getBalance(off.getName());
            } catch (Throwable e2) {
                return 0D;
            }
        }
    }
}