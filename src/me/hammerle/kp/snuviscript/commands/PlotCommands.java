/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.overrides.ModEntityPlayerMP;
import me.km.plots.PlotMap;
import me.km.plots.WorldPlotMap;
import me.km.utils.Location;
import net.minecraft.world.World;

public class PlotCommands {
    public static void registerFunctions(ScriptManager sm, WorldPlotMap plots) {
        sm.registerFunction("plot.get", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return plots.getPlots(l.getWorld(), l.getBlockPos());
        });
        sm.registerFunction("plot.check", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ModEntityPlayerMP p = (ModEntityPlayerMP) in[1].get(sc);
            int flags = in[2].getInt(sc);
            boolean empty = in[3].getBoolean(sc);
            return plots.canDoSomething(l.getWorld(), l.getBlockPos(), p, flags, empty);
        });
        sm.registerConsumer("plot.setflags", (sc, in) -> {
            PlotMap.Plot p = (PlotMap.Plot) in[0].get(sc);
            p.setFlag(in[1].getInt(sc), in[2].getBoolean(sc));
        });
        sm.registerFunction("plot.hasflags",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).hasFlags(in[1].getInt(sc)));
        sm.registerFunction("plot.getflags",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getFlags());
        sm.registerFunction("plot.getowners",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getOwners());
        sm.registerFunction("plot.add", (sc, in) -> {
            Location l1 = (Location) in[0].get(sc);
            Location l2 = (Location) in[1].get(sc);
            if(l1.getWorld() != l2.getWorld()) {
                throw new IllegalArgumentException("worlds not equal for locations");
            }
            if(in.length > 2) {
                return plots.add(l1.getWorld(), l1.getBlockPos(), l2.getBlockPos(),
                        in[2].getInt(sc));
            }
            return plots.add(l1.getWorld(), l1.getBlockPos(), l2.getBlockPos());
        });
        sm.registerConsumer("plot.remove", (sc, in) -> {
            plots.remove((World) in[1].get(sc), (PlotMap.Plot) in[0].get(sc));
        });
        sm.registerFunction("plot.getname", (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getName());
        sm.registerConsumer("plot.setname", (sc, in) -> {
            ((PlotMap.Plot) in[0].get(sc)).setName(in[1].getString(sc));
        });
        sm.registerFunction("plot.getid",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getId());
        sm.registerFunction("plot.iterator", (sc, in) -> {
            World word = (World) in[0].get(sc);
            if(in.length >= 2) {
                return plots.getIterator(word, CommandUtils.getUUID(in[1].get(sc)));
            }
            return plots.getIterator(word);
        });
        sm.registerFunction("plot.intersecting",
                (sc, in) -> plots.getIntersectingPlots((World) in[0].get(sc), in[1].getInt(sc),
                        in[2].getInt(sc), in[3].getInt(sc), in[4].getInt(sc), in[5].getInt(sc),
                        in[6].getInt(sc)));
        sm.registerFunction("plot.getminx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinX());
        sm.registerFunction("plot.getminy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinY());
        sm.registerFunction("plot.getminz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinZ());
        sm.registerFunction("plot.getmaxx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxX());
        sm.registerFunction("plot.getmaxy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxY());
        sm.registerFunction("plot.getmaxz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxZ());
    }
}
*/
