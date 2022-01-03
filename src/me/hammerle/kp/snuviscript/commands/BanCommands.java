package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import java.util.Date;
import java.util.GregorianCalendar;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

public class BanCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("ban.kick", (sc, in) -> {
            ((Player) in[0].get(sc)).kick((Component) in[1].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("ban.add", (sc, in) -> {
            String name = in[0].getString(sc);
            String reason = in[1].getString(sc);
            String banner = in[2].getString(sc);
            Date d = null;
            if(in.length >= 4) {
                GregorianCalendar calender = (GregorianCalendar) in[3].get(sc);
                d = new Date(calender.getTimeInMillis());
            }
            Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, d, banner);
        });
        KajetansPlugin.scriptManager.registerConsumer("ban.remove", (sc, in) -> {
            Bukkit.getBanList(BanList.Type.NAME).pardon(in[0].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("whitelist.enable", (sc, in) -> {
            Bukkit.setWhitelist(true);
        });
        KajetansPlugin.scriptManager.registerConsumer("whitelist.disable", (sc, in) -> {
            Bukkit.setWhitelist(false);
        });
        KajetansPlugin.scriptManager.registerConsumer("whitelist.add", (sc, in) -> {
            Bukkit.getWhitelistedPlayers()
                    .add(Bukkit.getOfflinePlayer(CommandUtils.getUUID(in[0].get(sc))));
        });
        KajetansPlugin.scriptManager.registerConsumer("whitelist.remove", (sc, in) -> {
            Bukkit.getWhitelistedPlayers()
                    .remove(Bukkit.getOfflinePlayer(CommandUtils.getUUID(in[0].get(sc))));
        });
        KajetansPlugin.scriptManager.registerConsumer("whitelist.clear", (sc, in) -> {
            Bukkit.getWhitelistedPlayers().clear();
        });
    }
}
