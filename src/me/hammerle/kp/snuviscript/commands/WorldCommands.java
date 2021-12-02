/*package me.km.snuviscript.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;
import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.Location;
import me.km.utils.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class WorldCommands {
    @SuppressWarnings("")
    public static void registerFunctions(ScriptManager sm, MinecraftServer server) {
        sm.registerAlias("players.toworldlist", "world.getplayers");
        sm.registerFunction("world.get", (sc, in) -> Utils.getWorldFromName(server, in[0].getString(sc)));
        sm.registerFunction("world.getname", (sc, in) -> Utils.getWorldName((World) in[0].get(sc)));
        sm.registerConsumer("world.setdifficulty", (sc, in) -> {
            server.setDifficultyForAllWorlds(Difficulty.valueOf(in[0].getString(sc).toUpperCase()), true);
        });
        sm.registerConsumer("world.setspawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            ((ServerWorld) l.getWorld()).func_241124_a__(l.getBlockPos(), in.length >= 2 ? in[1].getFloat(sc) : 0.0f);
        });
        sm.registerFunction("world.getspawn", (sc, in) -> {
            ServerWorld w = (ServerWorld) in[0].get(sc);
            BlockPos pos = w.getSpawnPoint();
            return new Location(w, pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f);
        });
        sm.registerFunction("world.getall", (sc, in) -> {
            ArrayList<World> worlds = new ArrayList<>();
            for(World w : server.getWorlds()) {
                worlds.add(w);
            }
            return worlds;
        });
        sm.registerConsumer("world.settime", (sc, in) -> ((ServerWorld) in[0].get(sc)).setDayTime(in[1].getLong(sc)));
        sm.registerFunction("world.gettime", (sc, in) -> (double) ((World) in[0].get(sc)).getDayTime());
        sm.registerFunction("world.hasstorm", (sc, in) -> ((World) in[0].get(sc)).isRaining());
        sm.registerConsumer("world.clearweather", (sc, in) -> {
            ((ServerWorld) in[0].get(sc)).func_241113_a_(in[1].getInt(sc), 0, false, false);
        });
        sm.registerConsumer("world.setrain", (sc, in) -> {
            ((ServerWorld) in[0].get(sc)).getWorld().func_241113_a_(0, in[1].getInt(sc), true, false);
        });
        sm.registerConsumer("world.setthunder", (sc, in) -> {
            ((ServerWorld) in[0].get(sc)).getWorld().func_241113_a_(0, in[1].getInt(sc), true, true);
        });
        sm.registerFunction("world.getentities", (sc, in) -> {
            return ((ServerWorld) in[0].get(sc)).getEntities().collect(Collectors.toList());
        });
    }
}
*/
