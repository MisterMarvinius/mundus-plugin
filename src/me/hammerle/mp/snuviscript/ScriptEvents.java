package me.hammerle.mp.snuviscript;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.connection.PlayerLoginConnection;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.snuviscript.code.Script;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.hammerle.snuviscript.inputprovider.Variable;
import net.kyori.adventure.text.Component;

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
        MundusPlugin.scriptManager.callEvent(name, sc -> {
            c.accept(sc);
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
        });
    }

    private static void handleEvent(String name, Consumer<Script> c) {
        MundusPlugin.scriptManager.callEvent(name, c, ScriptEvents::nothing);
    }

    private static void handleVar(Script sc, String event, String name, Consumer<Variable> c) {
        try {
            Variable v = sc.getVar(name);
            if(v != null) {
                c.accept(v);
            }
        } catch(Exception ex) {
            MundusPlugin.logger.print(String.format("invalid var in '%s' event", event), ex, null,
                    sc.getName(), sc, sc.getStackTrace());
        }
    }

    public static void onPlayerDataTick(Player p, String var) {
        MundusPlugin.scriptManager.callEvent("player_data_tick", sc -> {
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
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();
        handleEvent("inv_close", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", inv);
            sc.setVar("inv_title", e.getView().title());
        });

        if(inv.getHolder() instanceof SnuviInventoryHolder) {
            SnuviInventoryHolder snuvi = (SnuviInventoryHolder) inv.getHolder();
            Location l = p.getLocation();
            for(int i = 0; i < inv.getSize(); i++) {
                if(snuvi.getSlotType(i) == SnuviInventoryHolder.SnuviSlotType.NORMAL) {
                    ItemStack stack = inv.getItem(i);
                    if(stack != null) {
                        HashMap<Integer, ItemStack> map = p.getInventory().addItem(stack);
                        for(ItemStack left : map.values()) {
                            l.getWorld().dropItem(l, left);
                        }
                    }
                }
            }
        }
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
        String cause = e.getCause().name();
        Block damagerBlock = getBlock(e);
        Entity damagerEntity = getEntity(e);
        DamageSource damageSource = e.getDamageSource();
        handleEvent(e, "entity_damage", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("damage_source", damageSource);
            sc.setVar("cause", cause);
            sc.setVar("damager_block", damagerBlock);
            sc.setVar("damager_entity", damagerEntity);
            sc.setVar("raw_damage", e.getDamage());
            sc.setVar("damage", e.getFinalDamage());
        });
    }

    public static void onEntityRegainHealth(EntityRegainHealthEvent e) {
        String cause = e.getRegainReason().name();
        MundusPlugin.scriptManager.callEvent("entity_heal", sc -> {
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
        MundusPlugin.scriptManager.callEvent("living_death", sc -> {
            LivingEntity living = e.getEntity();
            setLiving(sc, living);
            DamageSource damageSource = living.getLastDamageCause().getDamageSource();
            sc.setVar("damage_source", damageSource);
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
            sc.setVar("old_material", e.getBlockState().getBlockData().getMaterial());
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

    public static void onBlockBurn(BlockBurnEvent e) {
        handleEvent(e, "block_burn", (sc) -> {
            setBlock(sc, e.getBlock());
            sc.setVar("block_source", e.getIgnitingBlock());
        });
    }

    public static void onBlockSpread(BlockSpreadEvent e) {
        handleEvent(e, "block_spread", (sc) -> {
            setBlock(sc, e.getNewState().getBlock());
            sc.setVar("block_source", e.getSource());
        });
    }

    public static void onBlockFade(BlockFadeEvent e) {
        handleEvent(e, "block_fade", (sc) -> {
            setBlock(sc, e.getNewState().getBlock());
        });
    }

    public static void onPlayerConnectionValidateLoginEvent(PlayerConnectionValidateLoginEvent e) {
        PlayerConnection conn = e.getConnection();
        PlayerProfile profile = null;
        if(conn instanceof PlayerLoginConnection login) {
            profile = login.getAuthenticatedProfile();
        }
        if(profile == null) {
            return;
        }
        UUID uuid = profile != null ? profile.getId() : null;

        handleEvent("player_login", (sc) -> {
            sc.setVar("uuid", uuid);
            sc.setVar("result", e.isAllowed());
        });
    }

    public static void onPlayerJoin(PlayerJoinEvent e) {
        MundusPlugin.scriptManager.callEvent("player_join", sc -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("message", e.joinMessage());
        }, sc -> {
            handleVar(sc, "player_join", "message", v -> e.joinMessage((Component) v.get(sc)));
        });
    }

    public static void onPlayerQuit(PlayerQuitEvent e) {
        MundusPlugin.scriptManager.callEvent("player_quit", sc -> {
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
        MundusPlugin.scriptManager.callEvent("fishing", sc -> {
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

    public static void onPlayerShearBlock(PlayerShearBlockEvent e) {
        handleEvent(e, "block_shear", sc -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getBlock());
            setHand(sc, e.getHand());
            sc.setVar("drops", e.getDrops());
        });
    }

    public static void onBellRing(BellRingEvent e) {
        handleEvent(e, "bell_ring", sc -> {
            setBlock(sc, e.getBlock());
            setEntity(sc, e.getEntity());
            sc.setVar("direction", e.getDirection());
        });
    }

    public static void onPlayerStartSpectating(PlayerStartSpectatingEntityEvent e) {
        handleEvent(e, "spectation_start", sc -> {
            setPlayer(sc, e.getPlayer());
            setEntity(sc, e.getNewSpectatorTarget());
        });
    }

    public static void onPlayerStopSpectating(PlayerStopSpectatingEntityEvent e) {
        handleEvent(e, "spectation_stop", sc -> {
            setPlayer(sc, e.getPlayer());
            setEntity(sc, e.getSpectatorTarget());
        });
    }

    public static void onPlayerPickupExp(PlayerPickupExperienceEvent e) {
        handleEvent(e, "player_pickup_exp", sc -> {
            setPlayer(sc, e.getPlayer());
        });
    }

    public static void onPlayerElytraBoost(PlayerElytraBoostEvent e) {
        MundusPlugin.scriptManager.callEvent("elytra_boost", sc -> {
            setPlayer(sc, e.getPlayer());
            setHand(sc, e.getHand());
            sc.setVar("firework", e.getFirework());
            sc.setVar("consume", e.shouldConsume());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "elytra_boost", "consume", v -> e.setShouldConsume(v.getBoolean(sc)));
        });
    }

    public static void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
        MundusPlugin.scriptManager.callEvent("player_leash", sc -> {
            setPlayer(sc, e.getPlayer());
            setHand(sc, e.getHand());
            setEntity(sc, e.getEntity());
            sc.setVar("leashholder", e.getLeashHolder());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
        });
    }

    public static void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
        MundusPlugin.scriptManager.callEvent("player_unleash", sc -> {
            String cause = e.getReason().toString();
            sc.setVar("cause", cause);
            setPlayer(sc, e.getPlayer());
            setHand(sc, e.getHand());
            setEntity(sc, e.getEntity());
            sc.setVar("dropleash", e.isDropLeash());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "player_unleash", "dropleash", v -> e.setDropLeash(v.getBoolean(sc)));
        });
    }

    public static void onEntityUnleash(EntityUnleashEvent e) {
        MundusPlugin.scriptManager.callEvent("entity_unleash", sc -> {
            String cause = e.getReason().toString();
            sc.setVar("cause", cause);
            setEntity(sc, e.getEntity());
            sc.setVar("dropleash", e.isDropLeash());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "entity_unleash", "dropleash", v -> e.setDropLeash(v.getBoolean(sc)));
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
        MundusPlugin.scriptManager.callEvent("pre_craft", sc -> {
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

    public static void onEntityDismount(EntityDismountEvent e) {
        handleEvent(e, "entity_dismount", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("mount", e.getDismounted());
        });
    }

    public static void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        handleEvent("player_change_world", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("from", e.getFrom());
        });
    }

    public static boolean onCommand(Player p, String command, Command c) {
        WrappedBool wr = new WrappedBool(false);
        String name = c.getClass().getName();
        MundusPlugin.scriptManager.callEvent("command", sc -> {
            sc.setVar("player", p);
            sc.setVar("command", command);
            sc.setVar("permission", c.getPermission());
            sc.setVar("command_class", name);
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

    @SuppressWarnings("deprecation")
    public static void onChat(io.papermc.paper.event.player.ChatEvent e) {
        MundusPlugin.scriptManager.callEvent("chat", sc -> {
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
        MundusPlugin.scriptManager.callEvent("explosion", sc -> {
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

    public static void onMissingPermission(CommandSender cs, String command, String perm) {
        handleEvent("missing_perm", (sc) -> {
            sc.setVar("sender", cs);
            sc.setVar("command", command);
            sc.setVar("perm", perm);
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
            setLiving(sc, e.getEntity());
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
        double timestamp;
        try {
            timestamp = Double.parseDouble(data[4]);
        } catch(NumberFormatException ex) {
            return;
        }
        handleEvent("vote", (sc) -> {
            sc.setVar("from", data[1]);
            sc.setVar("name", data[2]);
            sc.setVar("ip", data[3]);
            sc.setVar("timestamp", timestamp);
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

    public static void onVehicleEnter(VehicleEnterEvent e) {
        handleEvent(e, "vehicle_enter", (sc) -> {
            setEntity(sc, e.getEntered());
            sc.setVar("vehicle", e.getVehicle());
        });
    }

    public static void onVehicleExit(VehicleExitEvent e) {
        handleEvent(e, "vehicle_exit", (sc) -> {
            setLiving(sc, e.getExited());
            sc.setVar("vehicle", e.getVehicle());
        });
    }

    public static void onPlayerEditBook(PlayerEditBookEvent e) {
        handleEvent(e, "player_edit_book", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("signing", e.isSigning());
        });
    }

    public static void onPlayerArmorChange(PlayerArmorChangeEvent e) {
        handleEvent("player_armor_change", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("new_item", e.getNewItem());
            sc.setVar("old_item", e.getOldItem());
        });
    }

    public static void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent e) {
        handleEvent(e, "player_take_lectern_book", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getLectern().getBlock());
        });
    }

    public static void onPlayerJump(PlayerJumpEvent e) {
        handleEvent(e, "player_jump", (sc) -> {
            setPlayer(sc, e.getPlayer());
        });
    }
}

