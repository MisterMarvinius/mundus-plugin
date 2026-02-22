package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;

public class BossBarCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("boss.create", (sc, in) -> {
            return Bukkit.createBossBar(in[0].getString(sc), BarColor.valueOf(in[1].getString(sc)),
                    BarStyle.valueOf(in[2].getString(sc)));
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("boss.addplayer", (sc, in) -> {
            ((BossBar) in[0].get(sc)).addPlayer((Player) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("boss.removeplayer", (sc, in) -> {
            ((BossBar) in[0].get(sc)).removePlayer((Player) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("boss.removeall", (sc, in) -> {
            ((BossBar) in[0].get(sc)).removeAll();
        });
        MundusPlugin.scriptManager.registerConsumer("boss.addflag", (sc, in) -> {
            ((BossBar) in[0].get(sc)).addFlag(BarFlag.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("boss.removeflag", (sc, in) -> {
            ((BossBar) in[0].get(sc)).removeFlag(BarFlag.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("boss.setprogress", (sc, in) -> {
            ((BossBar) in[0].get(sc)).setProgress(in[1].getDouble(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("boss.settitle", (sc, in) -> {
            ((BossBar) in[0].get(sc)).setTitle(in[1].getString(sc));
        });
    }
}
