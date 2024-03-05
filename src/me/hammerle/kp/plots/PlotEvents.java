package me.hammerle.kp.plots;

import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.ProjectileSource;

public class PlotEvents {
    private static boolean canBypass(Player p) {
        return p.hasPermission("plot.bypass");
    }

    private static boolean shouldBeProtected(Entity e) {
        EntityType type = e.getType();
        return !e.getType().isAlive() || e instanceof Animals || type == EntityType.ARMOR_STAND
                || type == EntityType.VILLAGER;
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
        Entity indirect = e.getDamageSource().getCausingEntity();
        Entity direct = e.getDamageSource().getDirectEntity();

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
        if(b.getState() instanceof Lectern) {
            Lectern l = (Lectern) b.getState();
            Inventory inv = l.getInventory();
            if(inv.getSize() > 0 && inv.getItem(0) != null) {
                return;
            }
        }
        Player p = e.getPlayer();
        if(!canBypass(p) && !WorldPlotMap.canInteractWithBlock(b.getLocation(), p)) {
            e.setCancelled(true);
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

    public static void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if(e.getEntity() instanceof Player) {
            return;
        }
        if(WorldPlotMap.hasPlotAt(e.getEntity().getLocation())) {
            e.setCancelled(true);
        }
    }

    public static void onBlockIgnite(BlockIgniteEvent e) {
        Entity ent = e.getIgnitingEntity();
        if(ent == null) {
            return;
        }
        Player p;
        if(ent instanceof Player) {
            p = (Player) ent;
        } else if(ent instanceof Projectile) {
            ProjectileSource source = ((Projectile) ent).getShooter();
            if(!(source instanceof Player)) {
                return;
            }
            p = (Player) source;
        } else {
            return;
        }
        if(!canBypass(p) && !WorldPlotMap.canInteractWithBlock(e.getBlock().getLocation(), p)) {
            e.setCancelled(true);
        }
    }

    public static void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent e) {
        Player p = e.getPlayer();
        if(!canBypass(p)
                && !WorldPlotMap.canInteractWithBlock(e.getLectern().getLocation(), p)) {
            e.setCancelled(true);
        }
    }
}
