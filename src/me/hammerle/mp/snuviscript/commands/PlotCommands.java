package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.plots.PlotMap;
import me.hammerle.mp.plots.WorldPlotMap;

public class PlotCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("plot.get", (sc, in) -> {
            return WorldPlotMap.getPlots((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.check", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Player p = (Player) in[1].get(sc);
            int flags = in[2].getInt(sc);
            boolean empty = in[3].getBoolean(sc);
            return WorldPlotMap.canDoSomething(l, p, flags, empty);
        });
        MundusPlugin.scriptManager.registerConsumer("plot.setflags", (sc, in) -> {
            PlotMap.Plot p = (PlotMap.Plot) in[0].get(sc);
            p.setFlag(in[1].getInt(sc), in[2].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.hasflags",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).hasFlags(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("plot.getflags",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getFlags());
        MundusPlugin.scriptManager.registerFunction("plot.getowners",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getOwners());
        MundusPlugin.scriptManager.registerFunction("plot.add", (sc, in) -> {
            Location l1 = (Location) in[0].get(sc);
            Location l2 = (Location) in[1].get(sc);
            if(l1.getWorld() != l2.getWorld()) {
                throw new IllegalArgumentException("worlds not equal for locations");
            }
            if(in.length > 2) {
                return WorldPlotMap.add(l1, l2, in[2].getInt(sc));
            }
            return WorldPlotMap.add(l1, l2);
        });
        MundusPlugin.scriptManager.registerConsumer("plot.remove", (sc, in) -> {
            WorldPlotMap.remove((World) in[1].get(sc), (PlotMap.Plot) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.getname",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getName());
        MundusPlugin.scriptManager.registerConsumer("plot.setname", (sc, in) -> {
            ((PlotMap.Plot) in[0].get(sc)).setName(in[1].getString(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.getid",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getId());
        MundusPlugin.scriptManager.registerFunction("plot.iterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            if(in.length >= 2) {
                return WorldPlotMap.getIterator(w, CommandUtils.getUUID(in[1].get(sc)));
            }
            return WorldPlotMap.getIterator(w);
        });
        MundusPlugin.scriptManager.registerFunction("plot.intersecting",
                (sc, in) -> WorldPlotMap.getIntersectingPlots((World) in[0].get(sc),
                        in[1].getInt(sc), in[2].getInt(sc), in[3].getInt(sc), in[4].getInt(sc),
                        in[5].getInt(sc), in[6].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("plot.getminx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinX());
        MundusPlugin.scriptManager.registerFunction("plot.getminy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinY());
        MundusPlugin.scriptManager.registerFunction("plot.getminz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinZ());
        MundusPlugin.scriptManager.registerFunction("plot.getmaxx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxX());
        MundusPlugin.scriptManager.registerFunction("plot.getmaxy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxY());
        MundusPlugin.scriptManager.registerFunction("plot.getmaxz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxZ());
        MundusPlugin.scriptManager.registerConsumer("plot.addblock", (sc, in) -> {
            WorldPlotMap.addInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("plot.removeblock", (sc, in) -> {
            WorldPlotMap.removeInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.hasblock", (sc, in) -> {
            return WorldPlotMap.hasInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.blockiterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            return WorldPlotMap.getBlockIterator(w);
        });
        MundusPlugin.scriptManager.registerFunction("plot.position.getx", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getX();
        });
        MundusPlugin.scriptManager.registerFunction("plot.position.gety", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getY();
        });
        MundusPlugin.scriptManager.registerFunction("plot.position.getz", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getZ();
        });
        MundusPlugin.scriptManager.registerConsumer("plot.saveplots", (sc, in) -> {
            WorldPlotMap.savePlots((World) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("plot.saveblocks", (sc, in) -> {
            WorldPlotMap.saveBlocks((World) in[0].get(sc));
        });
    }
}
