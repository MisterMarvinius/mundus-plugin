package me.hammerle.mp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.PlayerData;
import net.kyori.adventure.text.Component;

public class ScoreboardCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("sb.settitle", (sc, in) -> {
            PlayerData.get((Player) in[0].get(sc)).setTitle((Component) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("sb.add", (sc, in) -> {
            PlayerData.get((Player) in[0].get(sc)).setText(in[1].getInt(sc), in[2].getString(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("sb.remove", (sc, in) -> {
            PlayerData.get((Player) in[0].get(sc)).removeText(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("sb.addraw", (sc, in) -> {
            PlayerData.get((Player) in[0].get(sc)).setRaw(in[1].getInt(sc), in[2].getString(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("sb.removeraw", (sc, in) -> {
            PlayerData.get((Player) in[0].get(sc)).removeRaw(in[1].getString(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("sb.clear", (sc, in) -> {
            PlayerData.get((Player) in[0].get(sc)).clearTexts();
        });
    }
}
