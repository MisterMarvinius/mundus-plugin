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
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.kp.plots.PlotEvents;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.CommandTest;
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
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;

public class Events implements Listener {
    @EventHandler
    public void onServerTick(ServerTickEndEvent e) {
        if(CommandTest.noTick) {
            return;
        }
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
        if(CommandTest.noEvents) {
            return;
        }
        Player p = e.getPlayer();
        CommandManager.clearPermissions(p);
        ScriptEvents.onPlayerLogin(e);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        Player p = e.getPlayer();
        PlayerData.get(p).login(p);
        PlayerCommands.join(p);
        ScriptEvents.onPlayerJoin(e);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerQuit(e);
    }

    @EventHandler
    public void onPlayerSendCommandInfo(PlayerCommandSendEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        /*e.getCommands().clear();
        KajetansPlugin.scheduleTask(() -> {
            CommandManager.send(e.getPlayer());
        })*/;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onInventoryClick(e);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onInventoryClose(e);
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onGlideToggle(e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerPreRespawn(e);
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerPostRespawn(e);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onEntityDamage(e);
        ScriptEvents.onEntityDamage(e);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onHangingBreakByEntity(e);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityRegainHealth(e);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityDeath(e);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onProjectileHit(e);
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onBlockDropItemEvent(e);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onBlockPlace(e);
        ScriptEvents.onBlockPlace(e);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onBlockBreak(e);
        ScriptEvents.onBlockBreak(e);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onPlayerBucket(e);
        ScriptEvents.onPlayerBucket(e);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onPlayerBucket(e);
        ScriptEvents.onPlayerBucket(e);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onPlayerInteract(e);
        ScriptEvents.onPlayerInteract(e);
        CustomItems.onPlayerInteract(e);
        e.setUseItemInHand(Event.Result.ALLOW);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onPlayerInteractEntity(e);
        ScriptEvents.onPlayerInteractEntity(e);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onPlayerInteractAtEntity(e);
    }

    @EventHandler
    public void onPlayerArmSwing(PlayerArmSwingEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerArmSwing(e);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerItemConsume(e);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerFish(e);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onCraftItem(e);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        CustomItems.onPrepareItemCraft(e);
        ScriptEvents.onPrepareItemCraft(e);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerDropItem(e);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityPickupItem(e);
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityMount(e);
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityDismount(e);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerChangedWorld(e);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerItemHeld(e);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerSwapHandItems(e);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onChat(io.papermc.paper.event.player.ChatEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onChat(e);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onExplosionPrime(e);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntitySpawn(e);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onCreatureSpawn(e);
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityRemoveFromWorld(e);
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onEntityTame(e);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerToggleSneak(e);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
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
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onWorldLoad(e);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerTeleport(e);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onEntityExplode(e);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onBlockExplode(e);
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onLightningStrike(e);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onBlockIgnite(e);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        CustomItems.onPlayerItemDamage(e);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        CustomItems.onPrepareAnvil(e);
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        CustomItems.onPrepareItemEnchant(e);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onVehicleEnter(e);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onVehicleExit(e);
    }

    private static boolean isVanillaWorld(World w) {
        String name = w.getName();
        return name.equals("world") || name.equals("world_nether") || name.equals("world_the_end");
    }

    @EventHandler
    public void onPortalCreateEvent(PortalCreateEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        if(!isVanillaWorld(e.getWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
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
        if(CommandTest.noEvents) {
            return;
        }
        World from = e.getFrom().getWorld();
        World to = e.getTo().getWorld();
        if(!isVanillaWorld(from) || !isVanillaWorld(to)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerEditBook(e);
    }

    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerArmorChange(e);
    }

    @EventHandler
    public void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        PlotEvents.onPlayerTakeLecternBook(e);
        ScriptEvents.onPlayerTakeLecternBook(e);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent e) {
        if(CommandTest.noEvents) {
            return;
        }
        ScriptEvents.onPlayerJump(e);
    }
}
