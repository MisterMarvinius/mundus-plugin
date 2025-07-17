package me.hammerle.mp.snuviscript.commands;

import me.hammerle.mp.MundusPlugin;
import java.net.InetAddress;
import java.util.Date;
import java.util.GregorianCalendar;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;

public class BanCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("ban.kick", (sc, in) -> {
            ((Player) in[0].get(sc)).kick((Component) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("ban.add", (sc, in) -> {
            String name = in[0].getString(sc);
            String reason = in[1].getString(sc);
            String banner = in[2].getString(sc);
            Date d = null;
            if(in.length >= 4) {
                GregorianCalendar calender = (GregorianCalendar) in[3].get(sc);
                d = new Date(calender.getTimeInMillis());
            }
            OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
            PlayerProfile profile = Bukkit.createProfile(offline.getUniqueId(), name);

            Bukkit.getBanList(BanListType.PROFILE)
                    .addBan(profile, reason, d, banner);
        });
        MundusPlugin.scriptManager.registerConsumer("ban.addip", (sc, in) -> {
            String ip = in[0].getString(sc);
            String reason = in[1].getString(sc);
            String banner = in[2].getString(sc);
            Date d = null;
            if(in.length >= 4) {
                GregorianCalendar calender = (GregorianCalendar) in[3].get(sc);
                d = new Date(calender.getTimeInMillis());
            }
            InetAddress addr = InetAddress.getByName(ip);
            byte[] bytes = addr.getAddress();

            Bukkit.getBanList(BanListType.IP).addBan(InetAddress.getByAddress(bytes), reason, d,
                    banner);
        });
        MundusPlugin.scriptManager.registerConsumer("ban.removeip", (sc, in) -> {
            String ip = in[0].getString(sc);
            InetAddress addr = InetAddress.getByName(ip);
            byte[] bytes = addr.getAddress();
            Bukkit.getBanList(BanListType.IP).pardon(InetAddress.getByAddress(bytes));
        });
        MundusPlugin.scriptManager.registerConsumer("ban.remove", (sc, in) -> {
            String name = in[0].getString(sc);
            Bukkit.getBanList(BanListType.PROFILE)
                    .pardon(Bukkit.createProfile(CommandUtils.getUUID(name)));
        });
        MundusPlugin.scriptManager.registerConsumer("whitelist.enable", (sc, in) -> {
            Bukkit.setWhitelist(true);
        });
        MundusPlugin.scriptManager.registerConsumer("whitelist.disable", (sc, in) -> {
            Bukkit.setWhitelist(false);
        });
        MundusPlugin.scriptManager.registerConsumer("whitelist.add", (sc, in) -> {
            Bukkit.getWhitelistedPlayers()
                    .add(Bukkit.getOfflinePlayer(CommandUtils.getUUID(in[0].get(sc))));
        });
        MundusPlugin.scriptManager.registerConsumer("whitelist.remove", (sc, in) -> {
            Bukkit.getWhitelistedPlayers()
                    .remove(Bukkit.getOfflinePlayer(CommandUtils.getUUID(in[0].get(sc))));
        });
        MundusPlugin.scriptManager.registerConsumer("whitelist.clear", (sc, in) -> {
            Bukkit.getWhitelistedPlayers().clear();
        });
    }
}
