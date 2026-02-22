package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.plots.PlotMap;
import me.hammerle.mp.plots.WorldPlotMap;

public class PlotCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("legacyplot.get", (sc, in) -> {
            return WorldPlotMap.getPlots((Location) in[0].get(sc));
        }, "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.check", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Player p = (Player) in[1].get(sc);
            int flags = in[2].getInt(sc);
            boolean empty = in[3].getBoolean(sc);
            return WorldPlotMap.canDoSomething(l, p, flags, empty);
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("legacyplot.setflags", (sc, in) -> {
            PlotMap.Plot p = (PlotMap.Plot) in[0].get(sc);
            p.setFlag(in[1].getInt(sc), in[2].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("legacyplot.hasflags",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).hasFlags(in[1].getInt(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getflags",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getFlags(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getowners",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getOwners(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.add", (sc, in) -> {
            Location l1 = (Location) in[0].get(sc);
            Location l2 = (Location) in[1].get(sc);
            if(l1.getWorld() != l2.getWorld()) {
                throw new IllegalArgumentException("worlds not equal for locations");
            }
            if(in.length > 2) {
                return WorldPlotMap.add(l1, l2, in[2].getInt(sc));
            }
            return WorldPlotMap.add(l1, l2);
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("legacyplot.remove", (sc, in) -> {
            WorldPlotMap.remove((World) in[1].get(sc), (PlotMap.Plot) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("legacyplot.getname",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getName(), "object");
        MundusPlugin.scriptManager.registerConsumer("legacyplot.setname", (sc, in) -> {
            ((PlotMap.Plot) in[0].get(sc)).setName(in[1].getString(sc));
        });
        MundusPlugin.scriptManager.registerFunction("legacyplot.getid",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getId(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.iterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            if(in.length >= 2) {
                return WorldPlotMap.getIterator(w, CommandUtils.getUUID(in[1].get(sc)));
            }
            return WorldPlotMap.getIterator(w);
        }, "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.intersecting",
                (sc, in) -> WorldPlotMap.getIntersectingPlots((World) in[0].get(sc),
                        in[1].getInt(sc), in[2].getInt(sc), in[3].getInt(sc), in[4].getInt(sc),
                        in[5].getInt(sc), in[6].getInt(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getminx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinX(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getminy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinY(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getminz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinZ(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getmaxx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxX(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getmaxy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxY(), "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.getmaxz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxZ(), "object");
        MundusPlugin.scriptManager.registerConsumer("legacyplot.addblock", (sc, in) -> {
            WorldPlotMap.addInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("legacyplot.removeblock", (sc, in) -> {
            WorldPlotMap.removeInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("legacyplot.hasblock", (sc, in) -> {
            return WorldPlotMap.hasInteractBlock((Location) in[0].get(sc));
        }, "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.blockiterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            return WorldPlotMap.getBlockIterator(w);
        }, "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.position.getx", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getX();
        }, "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.position.gety", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getY();
        }, "object");
        MundusPlugin.scriptManager.registerFunction("legacyplot.position.getz", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getZ();
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("legacyplot.saveplots", (sc, in) -> {
            WorldPlotMap.savePlots((World) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("legacyplot.saveblocks", (sc, in) -> {
            WorldPlotMap.saveBlocks((World) in[0].get(sc));
        });
    }
}
