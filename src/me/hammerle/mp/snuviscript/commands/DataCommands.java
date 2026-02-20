package me.hammerle.mp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.PlayerData;

public class DataCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("data.set", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            data.setData(in[1].getString(sc), in[2].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("data.settimer", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            data.setTimer(in[1].getString(sc), in[2].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("data.get", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            return data.getData(in[1].getString(sc));
        }, "object");
        MundusPlugin.scriptManager.registerFunction("data.gettimer", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            return (double) data.getTimer(in[1].getString(sc));
        }, "number");
        MundusPlugin.scriptManager.registerConsumer("data.clear", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerData data = PlayerData.get(p);
            data.clearData();
        });
    }
}
