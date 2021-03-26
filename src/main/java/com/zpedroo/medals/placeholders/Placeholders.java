package com.zpedroo.medals.placeholders;

import com.zpedroo.medals.Main;
import com.zpedroo.medals.vault.VaultHook;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import tech.folf.elosystem.EloSystem;

import java.text.NumberFormat;
import java.util.Locale;

public class Placeholders {

    public String replacePlaceholders(Player player, String str) {
        return StringUtils.replaceEach(str.replace("%custom%", Main.get().getPlayerManager().hasCustom(player) ? Main.get().getPlayerManager().getCustom(player).getCustomExtra() : ""), new String[] {
                "%kills%",
                "%kdr%",
                "%blocks%",
                "%duels%",
                "%mobs%",
                "%money%"
        }, new String[] {
                String.valueOf(EloSystem.getInstance().getDataManager().loadPlayer(player).getKills()),
                String.format("%.2f", EloSystem.getInstance().getDataManager().loadPlayer(player).getKdr()),
                formatNumber(com.zpedroo.mines.Main.get().getDataManager().loadPlayer(player).getBlocksTotal()),
                String.valueOf(com.zpedroo.x1.Main.get().getDataManager().loadPlayer(player).getWins()),
                formatNumber(com.zpedroo.outsider.Main.get().getDataManager().loadPlayer(player).getMobsTotal()),
                formatNumber(VaultHook.getBalance(player))
        });
    }

    private String formatNumber(double number) {
        if (number < 1000.0) {
            return this.fixNumber(number);
        }
        if (number < 1000000.0) {
            return this.fixNumber(number / 1000.0) + "k";
        }
        if (number < 1.0E9) {
            return this.fixNumber(number / 1000000.0) + "M";
        }
        if (number < 1.0E12) {
            return this.fixNumber(number / 1.0E9) + "B";
        }
        if (number < 1.0E15) {
            return this.fixNumber(number / 1.0E12) + "T";
        }
        if (number < 1.0E18) {
            return this.fixNumber(number / 1.0E15) + "Q";
        }
        if (number < 1.0E21) {
            return this.fixNumber(number / 1.0E18) + "QQ";
        }
        if (number < 1.0E24) {
            return this.fixNumber(number / 1.0E21) + "S";
        }
        if (number < 1.0E27) {
            return this.fixNumber(number / 1.0E24) + "SS";
        }
        if (number < 1.0E30) {
            return this.fixNumber(number / 1.0E27) + "O";
        }
        if (number < 1.0E33) {
            return this.fixNumber(number / 1.0E30) + "N";
        }
        if (number < 1.0E36) {
            return this.fixNumber(number / 1.0E33) + "D";
        }
        return String.valueOf(number);
    }

    private String fixNumber(double number) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        return format.format(number);
    }
}