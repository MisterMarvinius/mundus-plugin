package me.hammerle.kp.snuviscript;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.kp.NMS.Human;
import me.hammerle.snuviscript.code.Script;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.hammerle.snuviscript.inputprovider.Variable;
import net.kyori.adventure.text.Component;
import net.minecraft.world.damagesource.DamageSource;

public class ScriptEvents {
    private static class WrappedBool {
        public boolean wrapped;

        public WrappedBool(boolean init) {
            wrapped = init;
        }
    }

    private static void setEntity(Script sc, Entity ent) {
        sc.setVar("entity", ent);
    }

    private static void setLiving(Script sc, LivingEntity ent) {
        sc.setVar("living_entity", ent);
    }

    private static void setItem(Script sc, ItemStack stack) {
        sc.setVar("item", stack);
    }

    private static void setItem(Script sc, Item stack) {
        sc.setVar("item_entity", stack);
    }

    private static void setPlayer(Script sc, Player p) {
        sc.setVar("player", p);
    }

    private static void setCancel(Script sc, boolean b) {
        sc.setVar("cancel", b);
    }

    private static void setHand(Script sc, EquipmentSlot hand) {
        sc.setVar("hand", hand);
    }

    private static void setCancelled(Cancellable c, Script sc) {
        Variable v = sc.getVar("cancel");
        if(v == null) {
            return;
        }
        try {
            c.setCancelled(v.getBoolean(sc));
        } catch(Exception ex) {
        }
    }

    private static void setBlock(Script sc, Block b) {
        sc.setVar("block", b);
    }

    private static void nothing(Script sc) {}

    private static void handleEvent(Cancellable e, String name, Consumer<Script> c) {
        KajetansPlugin.scriptManager.callEvent(name, sc -> {
            c.accept(sc);
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
        });
    }

    private static void handleEvent(String name, Consumer<Script> c) {
        KajetansPlugin.scriptManager.callEvent(name, c, ScriptEvents::nothing);
    }

    private static void handleVar(Script sc, String event, String name, Consumer<Variable> c) {
        try {
            Variable v = sc.getVar(name);
            if(v != null) {
                c.accept(v);
            }
        } catch(Exception ex) {
            KajetansPlugin.logger.print(String.format("invalid var in '%s' event", event), ex, null,
                    sc.getName(), sc, sc.getStackTrace());
        }
    }

    public static void onPlayerDataTick(Player p, String var) {
        KajetansPlugin.scriptManager.callEvent("player_data_tick", sc -> {
            setPlayer(sc, p);
            sc.setVar("var", var);
        }, ScriptEvents::nothing);
    }

    public static void onGlideToggle(EntityToggleGlideEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        if(e.isGliding()) {
            handleEvent("player_elytra_start", sc -> {
                setPlayer(sc, p);
            });
        } else {
            handleEvent("player_elytra_stop", sc -> {
                setPlayer(sc, p);
            });
        }
    }

    public static void onPlayerMove(Player p, int id) {
        handleEvent("player_move", sc -> {
            setPlayer(sc, p);
            sc.setVar("id", (double) id);
        });
    }

    private static void onSnuviClick(Player p, Inventory inv, Component title, int slot,
            String click, String action) {
        handleEvent("snuvi_click", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", inv);
            sc.setVar("inv_title", title);
            sc.setVar("inv_slot", (double) slot);
            sc.setVar("click", click);
            sc.setVar("action", action);
        });
    }

    public static void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        Inventory clicked = e.getClickedInventory();
        int slot = e.getSlot();

        String click = e.getClick().toString();
        String action = e.getAction().toString();

        if(clicked != null && clicked.getHolder() instanceof SnuviInventoryHolder) {
            SnuviInventoryHolder holder = (SnuviInventoryHolder) clicked.getHolder();
            switch(holder.getSlotType(slot)) {
                case BLOCKED:
                    e.setCancelled(true);
                    return;
                case NORMAL:
                    break;
                case CLICK_EVENT_1:
                case CLICK_EVENT_2:
                    e.setCancelled(true);
                    onSnuviClick(p, e.getClickedInventory(), e.getView().title(), slot, click,
                            action);
                    return;
            }
        }
        handleEvent(e, "inv_click", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", e.getInventory());
            sc.setVar("inv_clicked", e.getClickedInventory());
            sc.setVar("inv_title", e.getView().title());
            sc.setVar("inv_slot", (double) slot);
            sc.setVar("click", click);
            sc.setVar("action", action);
        });
    }

    public static void onInventoryClose(InventoryCloseEvent e) {
        if(!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getPlayer();
        handleEvent("inv_close", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", e.getInventory());
            sc.setVar("inv_title", e.getView().title());
        });
    }

    public static boolean onHumanHurt(DamageSource ds, Human h, float amount) {
        WrappedBool wb = new WrappedBool(true);
        KajetansPlugin.scriptManager.callEvent("human_damage", sc -> {
            sc.setVar("human", h);
            sc.setVar("damage_source", ds);
            sc.setVar("damage", (double) amount);
            setCancel(sc, wb.wrapped);
        }, sc -> {
            Variable v = sc.getVar("cancel");
            if(v == null) {
                return;
            }
            try {
                wb.wrapped = v.getBoolean(sc);
            } catch(Exception ex) {
            }
        });
        return wb.wrapped;
    }

    public static void onPlayerPreRespawn(PlayerRespawnEvent e) {
        handleEvent("player_pre_respawn", sc -> setPlayer(sc, e.getPlayer()));
    }

    public static void onPlayerPostRespawn(PlayerPostRespawnEvent e) {
        handleEvent("player_post_respawn", sc -> setPlayer(sc, e.getPlayer()));
    }

    private static Block getBlock(EntityDamageEvent e) {
        if(e instanceof EntityDamageByBlockEvent) {
            EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
            return eb.getDamager();
        }
        return null;
    }

    private static Entity getEntity(EntityDamageEvent e) {
        if(e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            return ee.getDamager();
        }
        return null;
    }

    public static void onEntityDamage(EntityDamageEvent e) {
        String cause = e.getCause().toString();
        Block damagerBlock = getBlock(e);
        Entity damagerEntity = getEntity(e);
        handleEvent(e, "entity_damage", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("damage_source", NMS.getCurrentDamageSource());
            sc.setVar("cause", cause);
            sc.setVar("damager_block", damagerBlock);
            sc.setVar("damager_entity", damagerEntity);
            sc.setVar("raw_damage", e.getDamage());
            sc.setVar("damage", e.getFinalDamage());
        });
    }

    public static void onEntityRegainHealth(EntityRegainHealthEvent e) {
        String cause = e.getRegainReason().toString();
        KajetansPlugin.scriptManager.callEvent("entity_heal", sc -> {
            setEntity(sc, e.getEntity());
            sc.setVar("heal", e.getAmount());
            sc.setVar("cause", cause);
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "entity_heal", "heal", v -> e.setAmount(v.getDouble(sc)));
        });
    }

    public static void onEntityDeath(EntityDeathEvent e) {
        KajetansPlugin.scriptManager.callEvent("living_death", sc -> {
            setLiving(sc, e.getEntity());
            sc.setVar("damage_source", NMS.getCurrentDamageSource());
            sc.setVar("drops", e.getDrops());
            sc.setVar("experience", (double) e.getDroppedExp());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "living_death", "experience", v -> e.setDroppedExp(v.getInt(sc)));
        });
    }

    public static void onProjectileHit(ProjectileHitEvent e) {
        handleEvent(e, "projectile_hit", (sc) -> {
            sc.setVar("projectile", e.getEntity());
            sc.setVar("entity_hit", e.getHitEntity());
            sc.setVar("block_hit", e.getHitBlock());
            sc.setVar("shooter", e.getEntity().getShooter());
        });
    }

    public static void onBlockDropItemEvent(BlockDropItemEvent e) {
        final Block b = e.getBlockState().getBlock();
        handleEvent(e, "block_drop", sc -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("drops", e.getItems());
            setBlock(sc, b);
        });
    }

    public static void onBlockBreak(BlockBreakEvent e) {
        handleEvent(e, "block_break", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getBlock());
        });
    }

    public static void onBlockPlace(BlockPlaceEvent e) {
        handleEvent(e, "block_place", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getBlockPlaced());
            setHand(sc, e.getHand());
        });
    }

    public static void onPlayerLogin(PlayerLoginEvent e) {
        String result = e.getResult().toString();
        handleEvent("player_login", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("result", result);
        });
    }

    public static void onPlayerJoin(PlayerJoinEvent e) {
        KajetansPlugin.scriptManager.callEvent("player_join", sc -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("message", e.joinMessage());
        }, sc -> {
            handleVar(sc, "player_join", "message", v -> e.joinMessage((Component) v.get(sc)));
        });
    }

    public static void onPlayerQuit(PlayerQuitEvent e) {
        KajetansPlugin.scriptManager.callEvent("player_quit", sc -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("message", e.quitMessage());
        }, sc -> {
            handleVar(sc, "player_quit", "message", v -> e.quitMessage((Component) v.get(sc)));
        });
    }

    public static void onPlayerBucket(PlayerBucketEvent e) {
        handleEvent(e, "bucket_use", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getBlockClicked());
            sc.setVar("bucket", e.getBucket());
            setHand(sc, e.getHand());
        });
    }

    public static void onPlayerInteract(PlayerInteractEvent e) {
        String action = e.getAction().toString();
        handleEvent(e, "block_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getClickedBlock());
            setHand(sc, e.getHand());
            sc.setVar("action", action);
        });
    }

    public static void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        handleEvent(e, "entity_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setEntity(sc, e.getRightClicked());
            setHand(sc, e.getHand());
        });
    }

    public static void onPlayerArmSwing(PlayerArmSwingEvent e) {
        handleEvent(e, "arm_swing", sc -> {
            setPlayer(sc, e.getPlayer());
            setHand(sc, e.getHand());
        });
    }

    public static void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        handleEvent(e, "item_consume", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setItem(sc, e.getItem());
        });
    }

    public static void onPlayerFish(PlayerFishEvent e) {
        KajetansPlugin.scriptManager.callEvent("fishing", sc -> {
            setPlayer(sc, e.getPlayer());
            setEntity(sc, e.getCaught());
            sc.setVar("experience", (double) e.getExpToDrop());
            setCancel(sc, e.isCancelled());
            sc.setVar("hook", e.getHook());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "fishing", "experience", v -> e.setExpToDrop(v.getInt(sc)));
        });
    }

    public static void onCraftItem(CraftItemEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        handleEvent(e, "craft", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", e.getInventory());
            setItem(sc, e.getInventory().getResult());
        });
    }

    public static void onPrepareItemCraft(PrepareItemCraftEvent e) {
        KajetansPlugin.scriptManager.callEvent("pre_craft", sc -> {
            sc.setVar("players", e.getViewers());
            sc.setVar("inv", e.getInventory());
            setItem(sc, e.getInventory().getResult());
        }, sc -> {
            handleVar(sc, "pre_craft", "item",
                    v -> e.getInventory().setResult((ItemStack) v.get(sc)));
        });
    }

    public static void onPlayerDropItem(PlayerDropItemEvent e) {
        handleEvent(e, "player_drop", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setItem(sc, e.getItemDrop());
        });
    }

    public static void onEntityPickupItem(EntityPickupItemEvent e) {
        handleEvent(e, "living_pickup", (sc) -> {
            setLiving(sc, e.getEntity());
            setItem(sc, e.getItem());
        });
    }

    public static void onEntityMount(EntityMountEvent e) {
        handleEvent(e, "entity_mount", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("mount", e.getMount());
        });
    }

    public static void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        handleEvent("player_change_world", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("from", e.getFrom());
        });
    }

    public static boolean onCommand(Player p, String command) {
        WrappedBool wr = new WrappedBool(false);
        KajetansPlugin.scriptManager.callEvent("command", sc -> {
            sc.setVar("player", p);
            sc.setVar("command", command);
            setCancel(sc, wr.wrapped);
        }, sc -> {
            Variable v = sc.getVar("cancel");
            if(v == null) {
                return;
            }
            try {
                wr.wrapped = v.getBoolean(sc);
            } catch(Exception ex) {
            }
        });
        return wr.wrapped;
    }

    public static void onCustomCommand(CommandSender cs, String command, String[] args) {
        handleEvent("custom_command", (sc) -> {
            sc.setVar("sender", cs);
            sc.setVar("command", command);
            sc.setVar("args", Arrays.stream(args).map(s -> SnuviUtils.convert(s))
                    .collect(Collectors.toList()));
            sc.setVar("string_args", Arrays.stream(args).collect(Collectors.toList()));
        });
    }

    public static void onPlayerItemHeld(PlayerItemHeldEvent e) {
        handleEvent(e, "player_item_held", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("from", (double) e.getPreviousSlot());
            sc.setVar("to", (double) e.getNewSlot());
        });
    }

    public static void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        handleEvent("player_swap_hand_items", (sc) -> {
            setPlayer(sc, e.getPlayer());
        });
    }

    public static void onAsyncChat(AsyncChatEvent e) {
        KajetansPlugin.scriptManager.callEvent("chat", sc -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("message", e.message());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "chat", "message", v -> e.message((Component) v.get(sc)));
        });
    }

    public static void onExplosionPrime(ExplosionPrimeEvent e) {
        Entity ent = e.getEntity();
        e.setCancelled(true);
        KajetansPlugin.scriptManager.callEvent("explosion", sc -> {
            setEntity(sc, ent);
            sc.setVar("fire", e.getFire());
            sc.setVar("radius", (double) e.getRadius());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "explosion", "fire", v -> e.setFire(v.getBoolean(sc)));
            handleVar(sc, "explosion", "radius", v -> e.setRadius(v.getFloat(sc)));
        });
        if(!e.isCancelled()) {
            ent.getWorld().createExplosion(ent.getLocation(), e.getRadius(), e.getFire(), true,
                    ent);
            e.setFire(false);
            e.setRadius(0.0f);
        }
    }

    public static void onMissingCommand(CommandSender cs, String command) {
        handleEvent("missing_command", (sc) -> {
            sc.setVar("sender", cs);
            sc.setVar("command", command);
        });
    }

    public static void onMissingPermission(CommandSender cs, String command) {
        handleEvent("missing_perm", (sc) -> {
            sc.setVar("sender", cs);
            sc.setVar("command", command);
        });
    }

    public static void onEntitySpawn(EntitySpawnEvent e) {
        handleEvent(e, "entity_spawn", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("location", e.getLocation());
        });
    }

    public static void onCreatureSpawn(CreatureSpawnEvent e) {
        String cause = e.getSpawnReason().toString();
        handleEvent(e, "living_spawn", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("location", e.getLocation());
            sc.setVar("cause", cause);
        });
    }

    public static void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent e) {
        handleEvent("entity_remove", (sc) -> {
            setEntity(sc, e.getEntity());
        });
    }

    public static void onEntityTame(EntityTameEvent e) {
        handleEvent(e, "living_tame", (sc) -> {
            setLiving(sc, e.getEntity());
            sc.setVar("tamer", e.getOwner());
        });
    }

    public static void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        handleEvent(e, "player_toggle_sneak", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("sneak", e.isSneaking());
        });
    }

    public static void onEntityChangeBlock(EntityChangeBlockEvent e) {
        handleEvent(e, "entity_change_block", (sc) -> {
            setEntity(sc, e.getEntity());
            setBlock(sc, e.getBlock());
            sc.setVar("change_data", e.getBlockData());
        });
    }

    public static void onWorldLoad(WorldLoadEvent e) {
        handleEvent("world_load", (sc) -> {
            sc.setVar("world", e.getWorld());
        });
    }

    public static void onVote(String[] data) {
        if(data.length < 5) {
            return;
        }
        handleEvent("vote", (sc) -> {
            sc.setVar("from", data[1]);
            sc.setVar("name", data[2]);
            sc.setVar("ip", data[3]);
            sc.setVar("timestamp", SnuviUtils.convert(data[4]));
        });
    }

    public static void onPlayerTeleport(PlayerTeleportEvent e) {
        String cause = e.getCause().toString();
        handleEvent(e, "player_teleport", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("from", e.getFrom());
            sc.setVar("to", e.getTo());
            sc.setVar("cause", cause);
        });
    }
}

