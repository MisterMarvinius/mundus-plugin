package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.plots.WorldPlotMap;

public class WorldCommands {
    @SuppressWarnings("")
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("world.getplayers",
                (sc, in) -> new ArrayList<>(((World) in[0].get(sc)).getPlayers()), "list");
        MundusPlugin.scriptManager.registerFunction("world.get",
                (sc, in) -> Bukkit.getServer().getWorld(in[0].getString(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("world.getname",
                (sc, in) -> ((World) in[0].get(sc)).getName(), "string");
        MundusPlugin.scriptManager.registerConsumer("world.setdifficulty", (sc, in) -> {
            ((World) in[0].get(sc)).setDifficulty(Difficulty.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("world.setspawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            l.getWorld().setSpawnLocation(l);
        });
        MundusPlugin.scriptManager.registerFunction("world.getspawn",
                (sc, in) -> ((World) in[0].get(sc)).getSpawnLocation(), "location");
        MundusPlugin.scriptManager.registerFunction("world.getall",
                (sc, in) -> new ArrayList<>(Bukkit.getWorlds()), "list");
        MundusPlugin.scriptManager.registerConsumer("world.settime",
                (sc, in) -> ((World) in[0].get(sc)).setTime(in[1].getLong(sc)));
        MundusPlugin.scriptManager.registerFunction("world.gettime",
                (sc, in) -> (double) ((World) in[0].get(sc)).getTime(), "number");
        MundusPlugin.scriptManager.registerFunction("world.hasrain",
                (sc, in) -> ((World) in[0].get(sc)).hasStorm(), "boolean");
        MundusPlugin.scriptManager.registerFunction("world.hasthunder",
                (sc, in) -> ((World) in[0].get(sc)).isThundering(), "boolean");
        MundusPlugin.scriptManager.registerConsumer("world.clearweather", (sc, in) -> {
            World w = (World) in[0].get(sc);
            w.setStorm(false);
            w.setThundering(false);
            w.setClearWeatherDuration(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("world.setrain", (sc, in) -> {
            World w = (World) in[0].get(sc);
            w.setStorm(true);
            w.setWeatherDuration(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("world.setthunder", (sc, in) -> {
            World w = (World) in[0].get(sc);
            w.setThundering(true);
            w.setThunderDuration(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("world.getentities",
                (sc, in) -> new ArrayList<>(((World) in[0].get(sc)).getEntities()), "list");
        MundusPlugin.scriptManager.registerFunction("world.load", (sc, in) -> {
            World w = Bukkit.createWorld(WorldCreator.name(in[0].getString(sc)));
            WorldPlotMap.read(w.getName());
            return w;
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("world.setborder", (sc, in) -> {
            World w = (World) in[0].get(sc);
            Location l = (Location) in[1].get(sc);
            WorldBorder border = w.getWorldBorder();
            border.setCenter(l);
            border.setSize(in[2].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("world.unload",
                (sc, in) -> Bukkit.unloadWorld((World) in[0].get(sc), true), "boolean");
        MundusPlugin.scriptManager.registerFunction("world.getloadedchunks", (sc, in) -> {
            return ((World) in[0].get(sc)).getLoadedChunks();
        }, "array");
        MundusPlugin.scriptManager.registerFunction("world.unloadchunk", (sc, in) -> {
            return ((Chunk) in[0].get(sc)).unload();
        }, "boolean");
        MundusPlugin.scriptManager.registerFunction("world.isforceloadedchunk", (sc, in) -> {
            return ((Chunk) in[0].get(sc)).isForceLoaded();
        }, "boolean");
        MundusPlugin.scriptManager.registerFunction("world.getchunkx", (sc, in) -> {
            return (double) ((Chunk) in[0].get(sc)).getX();
        }, "number");
        MundusPlugin.scriptManager.registerFunction("world.getchunkz", (sc, in) -> {
            return (double) ((Chunk) in[0].get(sc)).getZ();
        }, "number");
    }
}
