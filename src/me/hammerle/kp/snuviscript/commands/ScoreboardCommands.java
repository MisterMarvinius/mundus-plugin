package me.hammerle.kp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.PlayerData;
import net.kyori.adventure.text.Component;

public class ScoreboardCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("sb.settitle", (sc, in) -> {
            Component title = (Component) in[1].get(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, cs -> {
                Player p = (Player) cs;
                PlayerData data = PlayerData.get(p);
                data.setTitle(title);
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("sb.add", (sc, in) -> {
            int id = in[1].getInt(sc);
            String message = in[2].getString(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, cs -> {
                Player p = (Player) cs;
                PlayerData data = PlayerData.get(p);
                data.setText(id, message);
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("sb.remove", (sc, in) -> {
            int id = in[1].getInt(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, cs -> {
                Player p = (Player) cs;
                PlayerData data = PlayerData.get(p);
                data.removeText(id);
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("sb.addraw", (sc, in) -> {
            int id = in[1].getInt(sc);
            String message = in[2].getString(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, cs -> {
                Player p = (Player) cs;
                PlayerData data = PlayerData.get(p);
                data.setRaw(id, message);
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("sb.removeraw", (sc, in) -> {
            String message = in[1].getString(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, cs -> {
                Player p = (Player) cs;
                PlayerData data = PlayerData.get(p);
                data.removeRaw(message);
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("sb.clear", (sc, in) -> {
            CommandUtils.doForGroup(in[0].get(sc), sc, cs -> {
                Player p = (Player) cs;
                PlayerData data = PlayerData.get(p);
                data.clearTexts();
            });
        });
    }
}
