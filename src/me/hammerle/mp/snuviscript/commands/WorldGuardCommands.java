package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.plots.PlotMap;

public class WorldGuardCommands {
    private static final Map<UUID, Set<PlotMap.Position>> INTERACT_BLOCKS = new HashMap<>();

    private static IntegerFlag flagsFlag;
    private static IntegerFlag idFlag;
    private static StringFlag nameFlag;

    public static void registerFunctions() {
        registerFlags();
        MundusPlugin.scriptManager.registerFunction("plot.get", (sc, in) -> {
            return getRegions((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.check", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Player p = (Player) in[1].get(sc);
            int flags = in[2].getInt(sc);
            boolean empty = in[3].getBoolean(sc);
            return canDoSomething(l, p, flags, empty);
        });
        MundusPlugin.scriptManager.registerConsumer("plot.setflags", (sc, in) -> {
            ProtectedRegion region = (ProtectedRegion) in[0].get(sc);
            setFlag(region, in[1].getInt(sc), in[2].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.hasflags", (sc, in) -> {
            return hasFlags((ProtectedRegion) in[0].get(sc), in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.getflags", (sc, in) -> {
            return (double) getFlags((ProtectedRegion) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.getowners", (sc, in) -> {
            return ((ProtectedRegion) in[0].get(sc)).getOwners().getUniqueIds();
        });
        MundusPlugin.scriptManager.registerFunction("plot.add", (sc, in) -> {
            Location l1 = (Location) in[0].get(sc);
            Location l2 = (Location) in[1].get(sc);
            if(l1.getWorld() != l2.getWorld()) {
                throw new IllegalArgumentException("worlds not equal for locations");
            }
            if(in.length > 2) {
                return addRegion(l1, l2, in[2].getInt(sc));
            }
            return addRegion(l1, l2, null);
        });
        MundusPlugin.scriptManager.registerConsumer("plot.remove", (sc, in) -> {
            removeRegion((World) in[1].get(sc), (ProtectedRegion) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.getname",
                (sc, in) -> getName((ProtectedRegion) in[0].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("plot.setname", (sc, in) -> {
            ((ProtectedRegion) in[0].get(sc)).setFlag(nameFlag, in[1].getString(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.getid",
                (sc, in) -> (double) getId((ProtectedRegion) in[0].get(sc)));
        MundusPlugin.scriptManager.registerFunction("plot.iterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            if(in.length >= 2) {
                return getIterator(w, CommandUtils.getUUID(in[1].get(sc)));
            }
            return getIterator(w);
        });
        MundusPlugin.scriptManager.registerFunction("plot.intersecting",
                (sc, in) -> getIntersectingPlots((World) in[0].get(sc), in[1].getInt(sc),
                        in[2].getInt(sc), in[3].getInt(sc), in[4].getInt(sc), in[5].getInt(sc),
                        in[6].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("plot.getminx",
                (sc, in) -> (double) ((ProtectedRegion) in[0].get(sc)).getMinimumPoint().getBlockX());
        MundusPlugin.scriptManager.registerFunction("plot.getminy",
                (sc, in) -> (double) ((ProtectedRegion) in[0].get(sc)).getMinimumPoint().getBlockY());
        MundusPlugin.scriptManager.registerFunction("plot.getminz",
                (sc, in) -> (double) ((ProtectedRegion) in[0].get(sc)).getMinimumPoint().getBlockZ());
        MundusPlugin.scriptManager.registerFunction("plot.getmaxx",
                (sc, in) -> (double) ((ProtectedRegion) in[0].get(sc)).getMaximumPoint().getBlockX());
        MundusPlugin.scriptManager.registerFunction("plot.getmaxy",
                (sc, in) -> (double) ((ProtectedRegion) in[0].get(sc)).getMaximumPoint().getBlockY());
        MundusPlugin.scriptManager.registerFunction("plot.getmaxz",
                (sc, in) -> (double) ((ProtectedRegion) in[0].get(sc)).getMaximumPoint().getBlockZ());
        MundusPlugin.scriptManager.registerConsumer("plot.addblock", (sc, in) -> {
            addInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("plot.removeblock", (sc, in) -> {
            removeInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.hasblock", (sc, in) -> {
            return hasInteractBlock((Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("plot.blockiterator", (sc, in) -> {
            World w = (World) in[0].get(sc);
            return getBlockIterator(w);
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
            savePlots((World) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("plot.saveblocks", (sc, in) -> {
            saveBlocks((World) in[0].get(sc));
        });
    }

    private static void registerFlags() {
        if(flagsFlag != null) {
            return;
        }
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        flagsFlag = registerFlag(registry, "mp-flags", IntegerFlag.class);
        idFlag = registerFlag(registry, "mp-id", IntegerFlag.class);
        nameFlag = registerFlag(registry, "mp-name", StringFlag.class);
    }

    private static <T extends Flag<?>> T registerFlag(FlagRegistry registry, String name,
            Class<T> type) {
        try {
            T flag = type.getConstructor(String.class).newInstance(name);
            registry.register(flag);
            return flag;
        } catch(FlagConflictException ex) {
            Flag<?> existing = registry.get(name);
            if(type.isInstance(existing)) {
                return type.cast(existing);
            }
            throw new IllegalStateException("WorldGuard flag conflict for " + name, ex);
        } catch(ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to register WorldGuard flag " + name, ex);
        }
    }

    private static List<ProtectedRegion> getRegions(Location l) {
        ApplicableRegionSet set =
                WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()
                        .getApplicableRegions(BukkitAdapter.adapt(l));
        if(set == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(set.getRegions());
    }

    private static boolean canDoSomething(Location l, Player p, int flags, boolean empty) {
        List<ProtectedRegion> regions = getRegions(l);
        if(regions.isEmpty()) {
            return empty;
        }
        UUID uuid = p == null ? null : p.getUniqueId();
        for(ProtectedRegion region : regions) {
            boolean allowedByFlag = flags == 0 || hasFlags(region, flags);
            boolean allowedByOwner = uuid != null
                    && (region.getOwners().contains(uuid) || region.getMembers().contains(uuid));
            if(allowedByFlag || allowedByOwner) {
                return true;
            }
        }
        return false;
    }

    private static void setFlag(ProtectedRegion region, int flag, boolean enabled) {
        int flags = getFlags(region);
        if(enabled) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
        region.setFlag(flagsFlag, flags);
    }

    private static boolean hasFlags(ProtectedRegion region, int flags) {
        return (getFlags(region) & flags) == flags;
    }

    private static int getFlags(ProtectedRegion region) {
        Integer flags = region.getFlag(flagsFlag);
        return flags == null ? 0 : flags;
    }

    private static ProtectedRegion addRegion(Location l1, Location l2, Integer id) {
        RegionManager manager = getRegionManager(l1.getWorld());
        if(manager == null) {
            throw new IllegalStateException("WorldGuard region manager missing for world");
        }
        int minX = Math.min(l1.getBlockX(), l2.getBlockX());
        int minY = Math.min(l1.getBlockY(), l2.getBlockY());
        int minZ = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int maxX = Math.max(l1.getBlockX(), l2.getBlockX());
        int maxY = Math.max(l1.getBlockY(), l2.getBlockY());
        int maxZ = Math.max(l1.getBlockZ(), l2.getBlockZ());
        String regionId = id == null ? ("plot-" + UUID.randomUUID()) : ("plot-" + id);
        ProtectedRegion region = new ProtectedCuboidRegion(regionId,
                BlockVector3.at(minX, minY, minZ), BlockVector3.at(maxX, maxY, maxZ));
        if(id != null) {
            region.setFlag(idFlag, id);
        }
        manager.addRegion(region);
        return region;
    }

    private static void removeRegion(World w, ProtectedRegion region) {
        RegionManager manager = getRegionManager(w);
        if(manager != null) {
            manager.removeRegion(region.getId());
        }
    }

    private static String getName(ProtectedRegion region) {
        String name = region.getFlag(nameFlag);
        return name == null ? region.getId() : name;
    }

    private static int getId(ProtectedRegion region) {
        Integer id = region.getFlag(idFlag);
        return id == null ? 0 : id;
    }

    private static Iterator<ProtectedRegion> getIterator(World w) {
        RegionManager manager = getRegionManager(w);
        if(manager == null) {
            return Collections.<ProtectedRegion>emptyList().iterator();
        }
        return manager.getRegions().values().iterator();
    }

    private static Iterator<ProtectedRegion> getIterator(World w, UUID uuid) {
        RegionManager manager = getRegionManager(w);
        if(manager == null) {
            return Collections.<ProtectedRegion>emptyList().iterator();
        }
        List<ProtectedRegion> regions = new ArrayList<>();
        for(ProtectedRegion region : manager.getRegions().values()) {
            if(region.getOwners().contains(uuid) || region.getMembers().contains(uuid)) {
                regions.add(region);
            }
        }
        return regions.iterator();
    }

    private static List<ProtectedRegion> getIntersectingPlots(World w, int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ) {
        RegionManager manager = getRegionManager(w);
        if(manager == null) {
            return Collections.emptyList();
        }
        int normMinX = Math.min(minX, maxX);
        int normMinY = Math.min(minY, maxY);
        int normMinZ = Math.min(minZ, maxZ);
        int normMaxX = Math.max(minX, maxX);
        int normMaxY = Math.max(minY, maxY);
        int normMaxZ = Math.max(minZ, maxZ);
        List<ProtectedRegion> list = new ArrayList<>();
        for(ProtectedRegion region : manager.getRegions().values()) {
            BlockVector3 rMin = region.getMinimumPoint();
            BlockVector3 rMax = region.getMaximumPoint();
            boolean intersects = rMin.getBlockX() <= normMaxX && rMax.getBlockX() >= normMinX
                    && rMin.getBlockY() <= normMaxY && rMax.getBlockY() >= normMinY
                    && rMin.getBlockZ() <= normMaxZ && rMax.getBlockZ() >= normMinZ;
            if(intersects) {
                list.add(region);
            }
        }
        return list;
    }

    private static void addInteractBlock(Location l) {
        Set<PlotMap.Position> blocks = INTERACT_BLOCKS.computeIfAbsent(l.getWorld().getUID(),
                key -> new HashSet<>());
        blocks.add(new PlotMap.Position(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
    }

    private static void removeInteractBlock(Location l) {
        Set<PlotMap.Position> blocks = INTERACT_BLOCKS.get(l.getWorld().getUID());
        if(blocks == null) {
            return;
        }
        blocks.remove(new PlotMap.Position(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
    }

    private static boolean hasInteractBlock(Location l) {
        Set<PlotMap.Position> blocks = INTERACT_BLOCKS.get(l.getWorld().getUID());
        if(blocks == null) {
            return false;
        }
        return blocks.contains(new PlotMap.Position(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
    }

    private static Iterator<PlotMap.Position> getBlockIterator(World w) {
        Set<PlotMap.Position> blocks = INTERACT_BLOCKS.get(w.getUID());
        if(blocks == null) {
            return Collections.<PlotMap.Position>emptyList().iterator();
        }
        return blocks.iterator();
    }

    private static void savePlots(World w) {
        RegionManager manager = getRegionManager(w);
        if(manager == null) {
            return;
        }
        try {
            manager.save();
        } catch(Exception ex) {
            MundusPlugin.warn(ex.getMessage());
        }
    }

    private static void saveBlocks(World w) {}

    private static RegionManager getRegionManager(World w) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
    }
}
