package me.hammerle.kp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.PlayerData;

public class DataCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("data.set", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            data.setData(in[1].getString(sc), in[2].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("data.settimer", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            data.setTimer(in[1].getString(sc), in[2].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("data.get", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            return data.getData(in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("data.get", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            return (double) data.getTimer(in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("data.clear", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            data.clearData();
        });
    }
}
