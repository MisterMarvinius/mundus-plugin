package me.hammerle.kp.plots;

import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.projectiles.ProjectileSource;
import me.hammerle.kp.NMS;

public class PlotEvents {
    private static boolean canBypass(Player p) {
        return p.hasPermission("plot.bypass");
    }

    private static boolean shouldBeProtected(Entity e) {
        EntityType type = e.getType();
        return !e.getType().isAlive() || e instanceof Animals || type == EntityType.ARMOR_STAND;
    }

    public static void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if(!canBypass(p) && !WorldPlotMap.canPlaceBlock(e.getBlockPlaced().getLocation(), p)) {
            e.setCancelled(true);
        }
    }

    public static void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if(!canBypass(p) && !WorldPlotMap.canBreakBlock(e.getBlock().getLocation(), p)) {
            e.setCancelled(true);
        }
    }

    public static void onEntityExplode(EntityExplodeEvent e) {
        Iterator<Block> iter = e.blockList().iterator();
        while(iter.hasNext()) {
            Block b = iter.next();
            if(WorldPlotMap.hasPlotAt(b.getLocation())) {
                iter.remove();
            }
        }
    }

    public static void onBlockExplode(BlockExplodeEvent e) {
        Iterator<Block> iter = e.blockList().iterator();
        while(iter.hasNext()) {
            Block b = iter.next();
            if(WorldPlotMap.hasPlotAt(b.getLocation())) {
                iter.remove();
            }
        }
    }

    public static void onPlayerBucket(PlayerBucketEvent e) {
        Block b = e.getBlockClicked();
        if(!canBypass(e.getPlayer())
                && !WorldPlotMap.canUseBucket(b.getLocation(), e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    public static void onEntityDamage(EntityDamageEvent e) {
        if(!shouldBeProtected(e.getEntity())) {
            return;
        }
        Entity indirect = NMS.getImmediateSource(NMS.getCurrentDamageSource());
        Entity direct = NMS.getTrueSource(NMS.getCurrentDamageSource());

        Player p;
        if(indirect instanceof Player) {
            p = (Player) indirect;
        } else if(direct instanceof Player) {
            p = (Player) direct;
        } else {
            return;
        }
        if(!canBypass(p) && !WorldPlotMap.canHitAmbientEntity(e.getEntity().getLocation(), p)) {
            e.setCancelled(true);
        }
    }

    public static void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
        Entity indirect = e.getRemover();
        ProjectileSource direct =
                (indirect instanceof Projectile) ? ((Projectile) indirect).getShooter() : null;

        Player p;
        if(indirect instanceof Player) {
            p = (Player) indirect;
        } else if(direct instanceof Player) {
            p = (Player) direct;
        } else {
            if(WorldPlotMap.hasPlotAt(e.getEntity().getLocation())) {
                e.setCancelled(true);
            }
            return;
        }
        if(!canBypass(p) && !WorldPlotMap.canHitAmbientEntity(e.getEntity().getLocation(), p)) {
            e.setCancelled(true);
        }
    }

    public static void onLightningStrike(LightningStrikeEvent e) {
        switch(e.getCause()) {
            case COMMAND:
            case CUSTOM:
            case TRIDENT:
                if(WorldPlotMap.hasPlotAt(e.getLightning().getLocation())) {
                    e.setCancelled(true);
                }
                break;
            default:
        }
    }

    public static void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL) {
            return;
        }
        Block b = e.getClickedBlock();
        if(b == null) {
            return;
        }
        Player p = e.getPlayer();
        if(!canBypass(p) && !WorldPlotMap.canInteractWithBlock(b.getLocation(), p)) {
            e.setCancelled(true);
            e.setUseItemInHand(Event.Result.ALLOW);
        }
    }

    public static void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if(!canBypass(p)
                && !WorldPlotMap.canInteractWithEntity(e.getRightClicked().getLocation(), p)) {
            e.setCancelled(true);
        }
    }

    public static void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if(!canBypass(p)
                && !WorldPlotMap.canInteractWithEntity(e.getRightClicked().getLocation(), p)) {
            e.setCancelled(true);
        }
    }
}
