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
            ((Player) in[0].get(sc)).kick(Component.text(in[1].getString(sc)));
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
    }
}
