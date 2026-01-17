package me.hammerle.mp.plots;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public final class PlotProtection {
    private PlotProtection() {}

    public static boolean canPlaceBlock(Location l, Player p) {
        return canBuild(l, p);
    }

    public static boolean canBreakBlock(Location l, Player p) {
        return canBuild(l, p);
    }

    public static boolean canUseBucket(Location l, Player p) {
        return canBuild(l, p);
    }

    public static boolean canHitAmbientEntity(Location l, Player p) {
        return canBuild(l, p);
    }

    public static boolean canInteractWithBlock(Location l, Player p) {
        return canBuild(l, p);
    }

    public static boolean canInteractWithEntity(Location l, Player p) {
        return canBuild(l, p);
    }

    public static boolean hasPlotAt(Location l) {
        ApplicableRegionSet regions = getRegions(l);
        if(regions == null) {
            return false;
        }
        for(com.sk89q.worldguard.protection.regions.ProtectedRegion region : regions) {
            if(!region.getId().equalsIgnoreCase("__global__")) {
                return true;
            }
        }
        return false;
    }

    private static ApplicableRegionSet getRegions(Location l) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(l));
    }

    private static boolean canBuild(Location l, Player p) {
        if(p == null) {
            return false;
        }
        WorldGuardPlugin plugin =
                (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testBuild(BukkitAdapter.adapt(l), plugin.wrapPlayer(p));
    }
}
