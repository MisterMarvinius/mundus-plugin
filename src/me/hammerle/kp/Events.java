package me.hammerle.kp;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.kp.plots.PlotEvents;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.MoveEvents;
import me.hammerle.kp.snuviscript.ScriptEvents;
import me.hammerle.kp.snuviscript.commands.PlayerCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;

public class Events implements Listener {
    @EventHandler
    public void onServerTick(ServerTickEndEvent e) {
        MoveEvents.tick();
        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerData.get(p).tick(p);
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if(CommandManager.execute(e.getPlayer(), e.getMessage())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        if(CommandManager.execute(e.getSender(), e.getCommand())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        CommandManager.clearPermissions(p);
        ScriptEvents.onPlayerLogin(e);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        NMS.patch(p);
        PlayerData.get(p).login(p);
        PlayerCommands.join(p);
        ScriptEvents.onPlayerJoin(e);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ScriptEvents.onPlayerQuit(e);
    }

    @EventHandler
    public void onPlayerSendCommandInfo(PlayerCommandSendEvent e) {
        e.getCommands().clear();
        KajetansPlugin.scheduleTask(() -> {
            CommandManager.send(e.getPlayer());
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ScriptEvents.onInventoryClick(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ScriptEvents.onInventoryClose(e);
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent e) {
        ScriptEvents.onGlideToggle(e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        ScriptEvents.onPlayerPreRespawn(e);
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent e) {
        ScriptEvents.onPlayerPostRespawn(e);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        PlotEvents.onEntityDamage(e);
        ScriptEvents.onEntityDamage(e);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
        PlotEvents.onHangingBreakByEntity(e);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        ScriptEvents.onEntityRegainHealth(e);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        ScriptEvents.onEntityDeath(e);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        ScriptEvents.onProjectileHit(e);
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent e) {
        ScriptEvents.onBlockDropItemEvent(e);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        PlotEvents.onBlockPlace(e);
        ScriptEvents.onBlockPlace(e);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        PlotEvents.onBlockBreak(e);
        ScriptEvents.onBlockBreak(e);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        PlotEvents.onPlayerBucket(e);
        ScriptEvents.onPlayerBucket(e);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        PlotEvents.onPlayerBucket(e);
        ScriptEvents.onPlayerBucket(e);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        PlotEvents.onPlayerInteract(e);
        ScriptEvents.onPlayerInteract(e);
        CustomItems.onPlayerInteract(e);
        e.setUseItemInHand(Event.Result.ALLOW);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        PlotEvents.onPlayerInteractEntity(e);
        ScriptEvents.onPlayerInteractEntity(e);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        PlotEvents.onPlayerInteractAtEntity(e);
    }

    @EventHandler
    public void onPlayerArmSwing(PlayerArmSwingEvent e) {
        ScriptEvents.onPlayerArmSwing(e);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        ScriptEvents.onPlayerItemConsume(e);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        ScriptEvents.onPlayerFish(e);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        ScriptEvents.onCraftItem(e);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        CustomItems.onPrepareItemCraft(e);
        ScriptEvents.onPrepareItemCraft(e);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        ScriptEvents.onPlayerDropItem(e);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        ScriptEvents.onEntityPickupItem(e);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent e) {
        ScriptEvents.onEntityMount(e);
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        ScriptEvents.onEntityDismount(e);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        ScriptEvents.onPlayerChangedWorld(e);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        ScriptEvents.onPlayerItemHeld(e);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        ScriptEvents.onPlayerSwapHandItems(e);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onChat(io.papermc.paper.event.player.ChatEvent e) {
        ScriptEvents.onChat(e);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        ScriptEvents.onExplosionPrime(e);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        ScriptEvents.onEntitySpawn(e);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        ScriptEvents.onCreatureSpawn(e);
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        ScriptEvents.onEntityRemoveFromWorld(e);
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        ScriptEvents.onEntityTame(e);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        ScriptEvents.onPlayerToggleSneak(e);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if(e.getBlock().getType() == Material.FARMLAND) {
            e.setCancelled(true);
        }
        if(e.getEntityType() != EntityType.FALLING_BLOCK) {
            PlotEvents.onEntityChangeBlock(e);
        }
        ScriptEvents.onEntityChangeBlock(e);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        ScriptEvents.onWorldLoad(e);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        ScriptEvents.onPlayerTeleport(e);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        PlotEvents.onEntityExplode(e);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        PlotEvents.onBlockExplode(e);
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        PlotEvents.onLightningStrike(e);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        PlotEvents.onBlockIgnite(e);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        CustomItems.onPlayerItemDamage(e);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        CustomItems.onPrepareAnvil(e);
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
        CustomItems.onPrepareItemEnchant(e);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        ScriptEvents.onVehicleEnter(e);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {
        ScriptEvents.onVehicleExit(e);
    }

    private static boolean isVanillaWorld(World w) {
        String name = w.getName();
        return name.equals("world") || name.equals("world_nether") || name.equals("world_the_end");
    }

    @EventHandler
    public void onPortalCreateEvent(PortalCreateEvent e) {
        if(!isVanillaWorld(e.getWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        World from = e.getFrom().getWorld();
        World to = e.getTo().getWorld();
        if(!isVanillaWorld(from) || !isVanillaWorld(to)) {
            e.setCancelled(true);
        }

        if(e.getEntity() instanceof Item) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        World from = e.getFrom().getWorld();
        World to = e.getTo().getWorld();
        if(!isVanillaWorld(from) || !isVanillaWorld(to)) {
            e.setCancelled(true);
        }
    }
}
