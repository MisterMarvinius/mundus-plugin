package me.hammerle.kp;

import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.kp.plots.PlotEvents;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.MoveEvents;
import me.hammerle.kp.snuviscript.ScriptEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        CommandManager.execute(e.getPlayer(), e.getMessage());
        e.setCancelled(true);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        CommandManager.execute(e.getSender(), e.getCommand());
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        CommandManager.clearPermissions(p);
        KajetansPlugin.scheduleTask(() -> PlayerData.get(p).login(p));
        ScriptEvents.onPlayerLogin(e);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
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
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        PlotEvents.onPlayerInteractEntity(e);
        ScriptEvents.onPlayerInteractEntity(e);
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
    public void onPlayerChangedWorl(PlayerChangedWorldEvent e) {
        ScriptEvents.onPlayerChangedWorl(e);
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
    public void onAsyncChat(AsyncChatEvent e) {
        ScriptEvents.onAsyncChat(e);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        ScriptEvents.onExplosionPrime(e);
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent e) {
        if(e.getEntity() instanceof Item) {
            e.setCancelled(true);
        }
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
}
