package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.plots.PlotMap;
import me.hammerle.kp.plots.WorldPlotMap;

public class PlotCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("plot.get", (sc, in) -> {
            return WorldPlotMap.getPlots((Location) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("plot.check", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Player p = (Player) in[1].get(sc);
            int flags = in[2].getInt(sc);
            boolean empty = in[3].getBoolean(sc);
            return WorldPlotMap.canDoSomething(l, p, flags, empty);
        });
        KajetansPlugin.scriptManager.registerConsumer("plot.setflags", (sc, in) -> {
            PlotMap.Plot p = (PlotMap.Plot) in[0].get(sc);
            p.setFlag(in[1].getInt(sc), in[2].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("plot.hasflags",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).hasFlags(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("plot.getflags",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getFlags());
        KajetansPlugin.scriptManager.registerFunction("plot.getowners",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getOwners());
        KajetansPlugin.scriptManager.registerFunction("plot.add", (sc, in) -> {
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
        KajetansPlugin.scriptManager.registerConsumer("plot.remove", (sc, in) -> {
            WorldPlotMap.remove((World) in[1].get(sc), (PlotMap.Plot) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("plot.getname",
                (sc, in) -> ((PlotMap.Plot) in[0].get(sc)).getName());
        KajetansPlugin.scriptManager.registerConsumer("plot.setname", (sc, in) -> {
            ((PlotMap.Plot) in[0].get(sc)).setName(in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("plot.getid",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getId());
        KajetansPlugin.scriptManager.registerFunction("plot.iterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            if(in.length >= 2) {
                return WorldPlotMap.getIterator(w, CommandUtils.getUUID(in[1].get(sc)));
            }
            return WorldPlotMap.getIterator(w);
        });
        KajetansPlugin.scriptManager.registerFunction("plot.intersecting",
                (sc, in) -> WorldPlotMap.getIntersectingPlots((World) in[0].get(sc),
                        in[1].getInt(sc), in[2].getInt(sc), in[3].getInt(sc), in[4].getInt(sc),
                        in[5].getInt(sc), in[6].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("plot.getminx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinX());
        KajetansPlugin.scriptManager.registerFunction("plot.getminy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinY());
        KajetansPlugin.scriptManager.registerFunction("plot.getminz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMinZ());
        KajetansPlugin.scriptManager.registerFunction("plot.getmaxx",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxX());
        KajetansPlugin.scriptManager.registerFunction("plot.getmaxy",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxY());
        KajetansPlugin.scriptManager.registerFunction("plot.getmaxz",
                (sc, in) -> (double) ((PlotMap.Plot) in[0].get(sc)).getMaxZ());
        KajetansPlugin.scriptManager.registerConsumer("plot.addblock", (sc, in) -> {
            WorldPlotMap.addInteractBlock((Location) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("plot.removeblock", (sc, in) -> {
            WorldPlotMap.removeInteractBlock((Location) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("plot.hasblock", (sc, in) -> {
            return WorldPlotMap.hasInteractBlock((Location) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("plot.blockiterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            return WorldPlotMap.getBlockIterator(w);
        });
        KajetansPlugin.scriptManager.registerFunction("plot.position.getx", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getX();
        });
        KajetansPlugin.scriptManager.registerFunction("plot.position.gety", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getY();
        });
        KajetansPlugin.scriptManager.registerFunction("plot.position.getz", (sc, in) -> {
            return (double) ((PlotMap.Position) in[0].get(sc)).getZ();
        });
        KajetansPlugin.scriptManager.registerConsumer("plot.saveplots", (sc, in) -> {
            WorldPlotMap.savePlots((World) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("plot.saveblocks", (sc, in) -> {
            WorldPlotMap.saveBlocks((World) in[0].get(sc));
        });
    }
}
