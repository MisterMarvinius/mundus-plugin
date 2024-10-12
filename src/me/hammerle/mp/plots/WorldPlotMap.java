package me.hammerle.mp.plots;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.hammerle.mp.plots.PlotMap.Plot;

public class WorldPlotMap {
    private final static HashMap<UUID, PlotMap> MAPS = new HashMap<>();
    private final static int PLACE_FLAG = (1 << 0);
    private final static int BREAK_FLAG = (1 << 1);
    private final static int BUCKET_FLAG = (1 << 2);
    private final static int HIT_AMBIENT_FLAG = (1 << 3);
    private final static int BLOCK_INTERACT_FLAG = (1 << 4);
    private final static int ENTITY_INTERACT_FLAG = (1 << 5);

    public static boolean canDoSomething(Location l, Player p, int flag, boolean empty) {
        PlotMap map = MAPS.get(l.getWorld().getUID());
        if(map == null) {
            return empty;
        }
        if(p == null) {
            return map.anyPlotMatches(l.getBlockX(), l.getBlockY(), l.getBlockZ(), empty,
                    plot -> plot.hasFlags(flag));
        }
        UUID uuid = p.getUniqueId();
        return map.anyPlotMatches(l.getBlockX(), l.getBlockY(), l.getBlockZ(), empty,
                plot -> plot.hasFlags(flag) || plot.getOwners().contains(uuid));
    }

    public static boolean canPlaceBlock(Location l, Player p) {
        return canDoSomething(l, p, PLACE_FLAG, true);
    }

    public static boolean canBreakBlock(Location l, Player p) {
        return canDoSomething(l, p, BREAK_FLAG, true);
    }

    public static boolean canUseBucket(Location l, Player p) {
        return canDoSomething(l, p, BUCKET_FLAG, true);
    }

    public static boolean canHitAmbientEntity(Location l, Player p) {
        return canDoSomething(l, p, HIT_AMBIENT_FLAG, true);
    }

    public static boolean canInteractWithBlock(Location l, Player p) {
        boolean canDo = canDoSomething(l, p, BLOCK_INTERACT_FLAG, true);
        if(!canDo) {
            PlotMap map = MAPS.get(l.getWorld().getUID());
            if(map != null && map.hasInteractBlock(l.getBlockX(), l.getBlockY(), l.getBlockZ())) {
                return true;
            }
        }
        return canDo;
    }

    public static boolean canInteractWithEntity(Location l, Player p) {
        return canDoSomething(l, p, ENTITY_INTERACT_FLAG, true);
    }

    public static List<PlotMap.Plot> getPlots(Location l) {
        PlotMap map = MAPS.get(l.getWorld().getUID());
        if(map == null) {
            return Collections.<PlotMap.Plot>emptyList();
        }
        return map.getPlotAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static boolean hasPlotAt(Location l) {
        PlotMap map = MAPS.get(l.getWorld().getUID());
        if(map == null) {
            return false;
        }
        return map.hasPlotAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    private static PlotMap getOrCreate(World w) {
        PlotMap map = MAPS.get(w.getUID());
        if(map == null) {
            map = new PlotMap();
            MAPS.put(w.getUID(), map);
        }
        return map;
    }

    public static PlotMap.Plot add(Location l1, Location l2) {
        return getOrCreate(l1.getWorld()).add(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(),
                l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
    }

    public static PlotMap.Plot add(Location l1, Location l2, int id) {
        return getOrCreate(l1.getWorld()).add(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(),
                l2.getBlockX(), l2.getBlockY(), l2.getBlockZ(), id);
    }

    public static void remove(World w, Plot p) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            map.remove(p);
        }
    }

    public static void addInteractBlock(Location l) {
        getOrCreate(l.getWorld()).addInteractBlock(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static void removeInteractBlock(Location l) {
        PlotMap map = MAPS.get(l.getWorld().getUID());
        if(map == null) {
            return;
        }
        map.removeInteractBlock(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static boolean hasInteractBlock(Location l) {
        PlotMap map = MAPS.get(l.getWorld().getUID());
        if(map == null) {
            return false;
        }
        return map.hasInteractBlock(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static Iterator<PlotMap.Position> getBlockIterator(World w) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            return map.getBlockIterator();
        }
        return Collections.<PlotMap.Position>emptyList().iterator();
    }

    public static Iterator<PlotMap.Plot> getIterator(World w) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            return map.getIterator();
        }
        return Collections.<PlotMap.Plot>emptyList().iterator();
    }

    public static Iterator<PlotMap.Plot> getIterator(World w, UUID uuid) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            return map.getIterator(uuid);
        }
        return Collections.<PlotMap.Plot>emptyList().iterator();
    }

    public static List<Plot> getIntersectingPlots(World w, int minX, int minY, int minZ, int maxX,
            int maxY, int maxZ) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            return map.getIntersectingPlots(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return Collections.<Plot>emptyList();
    }

    public static void savePlots(World w) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            map.scheduleSavePlots(w.getName());
        }
    }

    public static void saveBlocks(World w) {
        PlotMap map = MAPS.get(w.getUID());
        if(map != null) {
            map.scheduleSaveBlocks(w.getName());
        }
    }

    public static void read() {
        File dir = new File("plot_storage");
        if(!dir.exists()) {
            return;
        }
        for(File f : dir.listFiles()) {
            read(f.getName());
        }
    }

    public static boolean read(String worldName) {
        File dir = new File("plot_storage");
        if(!dir.exists()) {
            return true;
        }
        World w = Bukkit.getServer().getWorld(worldName);
        if(w == null) {
            return true;
        }
        PlotMap pm = new PlotMap();
        MAPS.put(w.getUID(), pm);
        pm.read(new File("plot_storage/" + worldName));
        pm.readInteractionBlocks(new File("plot_storage/" + worldName + "_blocks"));
        return false;
    }
}
