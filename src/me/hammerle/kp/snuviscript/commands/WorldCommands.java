package me.hammerle.kp.snuviscript.commands;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import me.hammerle.kp.KajetansPlugin;

public class WorldCommands {
    @SuppressWarnings("")
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("world.getplayers",
                (sc, in) -> new ArrayList<>(((World) in[0].get(sc)).getPlayers()));
        KajetansPlugin.scriptManager.registerFunction("world.get",
                (sc, in) -> Bukkit.getServer().getWorld(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("world.getname",
                (sc, in) -> ((World) in[0].get(sc)).getName());
        KajetansPlugin.scriptManager.registerConsumer("world.setdifficulty", (sc, in) -> {
            ((World) in[0].get(sc)).setDifficulty(Difficulty.valueOf(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerConsumer("world.setspawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            l.getWorld().setSpawnLocation(l);
        });
        KajetansPlugin.scriptManager.registerFunction("world.getspawn",
                (sc, in) -> ((World) in[0].get(sc)).getSpawnLocation());
        KajetansPlugin.scriptManager.registerFunction("world.getall",
                (sc, in) -> new ArrayList<>(Bukkit.getWorlds()));
        KajetansPlugin.scriptManager.registerConsumer("world.settime",
                (sc, in) -> ((World) in[0].get(sc)).setTime(in[1].getLong(sc)));
        KajetansPlugin.scriptManager.registerFunction("world.gettime",
                (sc, in) -> (double) ((World) in[0].get(sc)).getTime());
        KajetansPlugin.scriptManager.registerFunction("world.hasrain",
                (sc, in) -> ((World) in[0].get(sc)).hasStorm());
        KajetansPlugin.scriptManager.registerFunction("world.hasthunder",
                (sc, in) -> ((World) in[0].get(sc)).isThundering());
        KajetansPlugin.scriptManager.registerConsumer("world.clearweather", (sc, in) -> {
            World w = (World) in[0].get(sc);
            w.setStorm(false);
            w.setThundering(false);
            w.setClearWeatherDuration(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("world.setrain", (sc, in) -> {
            World w = (World) in[0].get(sc);
            w.setStorm(true);
            w.setWeatherDuration(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("world.setthunder", (sc, in) -> {
            World w = (World) in[0].get(sc);
            w.setThundering(true);
            w.setThunderDuration(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("world.getentities",
                (sc, in) -> new ArrayList<>(((World) in[0].get(sc)).getEntities()));
        KajetansPlugin.scriptManager.registerFunction("world.load",
                (sc, in) -> Bukkit.createWorld(WorldCreator.name(in[0].getString(sc))));
        KajetansPlugin.scriptManager.registerFunction("world.unload",
                (sc, in) -> Bukkit.unloadWorld((World) in[0].get(sc), true));
    }
}
