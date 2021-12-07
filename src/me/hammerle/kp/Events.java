package me.hammerle.kp;

import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.MoveEvents;
import me.hammerle.kp.snuviscript.ScriptEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;

public class Events implements Listener {
    private long lastMillis = 0;
    private int lastSlot = -1;
    private Player p;

    @EventHandler
    public void test(PlayerItemHeldEvent e) {
        long diff = System.currentTimeMillis() - lastMillis;
        if(diff < 300) {
            KajetansPlugin.log("CAST " + e.getNewSlot());
            lastMillis = 0;
            e.getPlayer().getInventory().setHeldItemSlot(lastSlot);
        }
    }

    @EventHandler
    public void test2(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
        lastSlot = e.getPlayer().getInventory().getHeldItemSlot();
        e.getPlayer().getInventory().setHeldItemSlot(8);
        lastMillis = System.currentTimeMillis();
        p = e.getPlayer();
    }

    @EventHandler
    public void onServerTick(ServerTickEndEvent e) {
        MoveEvents.tick();
        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerData.get(p).tick(p);
        }

        if(lastMillis != 0) {
            long diff = System.currentTimeMillis() - lastMillis;
            if(diff >= 500) {
                KajetansPlugin.log("SWAP");
                p.getInventory().setHeldItemSlot(lastSlot);
                lastMillis = 0;
            }
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
        ScriptEvents.onEntityDamage(e);
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
        ScriptEvents.onBlockPlace(e);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ScriptEvents.onBlockBreak(e);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        ScriptEvents.onPlayerBucket(e);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        ScriptEvents.onPlayerBucket(e);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ScriptEvents.onPlayerInteract(e);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
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
}
