package me.hammerle.kp.snuviscript;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.snuviscript.code.Script;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.hammerle.snuviscript.inputprovider.Variable;
import net.kyori.adventure.text.Component;

public class ScriptEvents {
    private static class WrappedBool {
        public boolean wrapped = false;
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

    private static void onSnuviClick(Player p, Inventory inv, Component title, int slot) {
        handleEvent("snuvi_click", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", inv);
            sc.setVar("inv_title", title);
            sc.setVar("inv_slot", (double) slot);
        });
    }

    public static void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        Inventory clicked = e.getClickedInventory();
        int slot = e.getSlot();

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
                    onSnuviClick(p, e.getClickedInventory(), e.getView().title(), slot);
                    return;
            }
        }
        String click = e.getClick().toString();
        String action = e.getAction().toString();
        handleEvent(e, "inv_click", sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", e.getInventory());
            sc.setVar("inv_clicked", e.getClickedInventory());
            sc.setVar("inv_name", e.getView().title());
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
            sc.setVar("inv_name", e.getView().title());
        });
    }

    /*public void onHumanHurt(Entity attacker, EntityHuman h) {
        handleEvent("human_hurt", sc -> {
            setEntity(sc, attacker);
            sc.setVar("human", h);
        });
    }*/

    public static void onPlayerPreRespawn(PlayerRespawnEvent e) {
        handleEvent("player_post_respawn", sc -> setPlayer(sc, e.getPlayer()));
    }

    public static void onPlayerPostRespawn(PlayerPostRespawnEvent e) {
        handleEvent("player_pre_respawn", sc -> setPlayer(sc, e.getPlayer()));
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
        handleEvent("entity_damage", (sc) -> {
            setEntity(sc, e.getEntity());
            sc.setVar("vanilla_cause", NMS.getCurrentDamageSource());
            sc.setVar("cause", cause);
            sc.setVar("damager_block", damagerBlock);
            sc.setVar("damager_entity", damagerEntity);
            sc.setVar("raw_damage", e.getDamage());
            sc.setVar("damage", e.getFinalDamage());
        });
    }

    public static void onEntityRegainHealth(EntityRegainHealthEvent e) {
        KajetansPlugin.scriptManager.callEvent("entity_heal", sc -> {
            setEntity(sc, e.getEntity());
            sc.setVar("heal_amount", e.getAmount());
            sc.setVar("heal_reason", e.getRegainReason().toString());
            setCancel(sc, e.isCancelled());
        }, sc -> {
            setCancelled(e, sc);
            handleVar(sc, "entity_heal", "heal_amount", v -> e.setAmount(v.getDouble(sc)));
        });
    }

    public static void onEntityDeath(EntityDeathEvent e) {
        KajetansPlugin.scriptManager.callEvent("living_death", sc -> {
            setLiving(sc, e.getEntity());
            sc.setVar("vanilla_cause", NMS.getCurrentDamageSource());
            sc.setVar("drops", e.getDrops());
            sc.setVar("experience", e.getDrops());
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
        handleEvent("block_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, e.getClickedBlock());
            setHand(sc, e.getHand());
        });
    }

    public static void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        handleEvent("entity_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setEntity(sc, e.getRightClicked());
            setHand(sc, e.getHand());
        });
    }

    public static void onPlayerArmSwing(PlayerArmSwingEvent e) {
        handleEvent("arm_swing", sc -> {
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
            sc.setVar("experience", e.getExpToDrop());
            setCancel(sc, e.isCancelled());
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
            setItem(sc, e.getInventory().getResult());
        });
    }

    public static void onPrepareItemCraft(PrepareItemCraftEvent e) {
        KajetansPlugin.scriptManager.callEvent("pre_craft", sc -> {
            sc.setVar("players", e.getViewers());
            setItem(sc, e.getInventory().getResult());
        }, sc -> {
            handleVar(sc, "pre_craft", "item",
                    v -> e.getInventory().setResult((ItemStack) v.get(sc)));
        });
    }

    public static void onPlayerDropItem(PlayerDropItemEvent e) {
        handleEvent(e, "player_toss", (sc) -> {
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

    public static void onPlayerChangedWorl(PlayerChangedWorldEvent e) {
        handleEvent("player_change_world", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("from", e.getFrom());
        });
    }

    public static boolean onCommand(Player p, String command) {
        WrappedBool wr = new WrappedBool();
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

    public static void onCustomCommand(Player p, String command, String[] args) {
        handleEvent("custom_command", (sc) -> {
            setPlayer(sc, p);
            sc.setVar("command", command);
            sc.setVar("args", Arrays.stream(args).map(s -> SnuviUtils.convert(s))
                    .collect(Collectors.toList()));
            sc.setVar("text_args", Arrays.stream(args).collect(Collectors.toList()));
        });
    }

    /*public void onFunctionKey(ServerPlayerEntity p, int key) {
        handleEvent("function_key", (sc) -> {
            setPlayer(sc, p);
            sc.setVar("key", (double) key);
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onChatEvent(ServerChatEvent e) {
        handleEvent("chat", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("message", e.getMessage());
            setCancel(sc, e.isCanceled());
        }, (sc) -> {
            handleVar(sc, "chat", "message",
                    v -> e.setComponent(new StringTextComponent(v.getString(sc))));
            handleVar(sc, "chat", "cancel", v -> e.setCanceled(v.getBoolean(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public void onPreExplosion(ExplosionEvent.Start e) {
        e.setCanceled(true);
        handleEvent(e, "pre_explosion", sc -> {
            sc.setVar("damage_source", e.getExplosion().getDamageSource());
            sc.setVar("location", new Location(e.getWorld(), e.getExplosion().getPosition()));
        });
    }
    
    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate e) {
        ReflectionUtils.setNoBreakMode(e.getExplosion());
        handleEvent(e, "explosion", sc -> {
            sc.setVar("affected_blocks", e.getAffectedBlocks());
            sc.setVar("affected_entities", e.getAffectedEntities());
            sc.setVar("damage_source", e.getExplosion().getDamageSource());
            sc.setVar("location", new Location(e.getWorld(), e.getExplosion().getPosition()));
        });
    }
    
    private static String getName(ICommandSource cs) {
        if(cs instanceof PlayerEntity) {
            return ((PlayerEntity) cs).getName().getString();
        } else if(cs instanceof MinecraftServer) {
            return "Server";
        }
        return null;
    }*/

    public static void onMissingCommand(CommandSender cs, String command) {
        //PlayerEntity p = (cs instanceof PlayerEntity) ? (PlayerEntity) cs : null;
        //handleEvent("missing_command", (sc) -> {
        //    setPlayer(sc, p);
        //    sc.setVar("command_name", command);
        //    sc.setVar("sender_name", getName(cs));
        //});
    }

    public static void onMissingPermission(CommandSender cs, String command) {
        //PlayerEntity p = (cs instanceof PlayerEntity) ? (PlayerEntity) cs : null;
        //handleEvent("missing_perm", (sc) -> {
        //    setPlayer(sc, p);
        //    sc.setVar("command_name", command);
        //    sc.setVar("sender_name", getName(cs));
        //});
    }

    /*@SubscribeEvent(receiveCanceled = true)
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        Entity ent = e.getEntity();
        BlockPos pos = ent.getPosition();
        Server.scheduler.scheduleTask("onEntityJoinWorld", () -> {
            World w = ent.getEntityWorld();
            if(w.isAreaLoaded(pos, 1) && ent.getEntityWorld().getBlockState(ent.getPosition())
                    .getBlock() == Blocks.NETHER_PORTAL) {
                ent.setLocationAndAngles(pos.getX(), pos.getY() + 10, pos.getZ(), ent.rotationYaw,
                        ent.rotationPitch);
                return;
            }
        });
        if(!ent.isPassenger() && !scripts.getEntityLimits().isAllowedToSpawn(ent.getType())
                && !ent.getTags().contains("mod_spawned")) {
            ent.getPassengers().forEach(rider -> {
                if(rider == null || rider instanceof PlayerEntity) {
                    return;
                }
                rider.remove();
            });
            ent.removePassengers();
            e.setCanceled(true);
            return;
        }
        handleEvent(e, "entity_join", (sc) -> {
            setEntity(sc, ent);
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingUpdate(LivingUpdateEvent e) {
        if(e.getEntity().getTags().contains("no_tick")) {
            e.setCanceled(true);
        }
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onEntityLeaveWorld(EntityLeaveWorldEvent e) {
        Entity ent = e.getEntity();
        handleEvent(e, "entity_leave", (sc) -> {
            setEntity(sc, ent);
        });
    }
    
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if(e.phase == TickEvent.Phase.END) {
            scripts.getEntityLimits().tick(server.getWorlds());
        }
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onAnimalTame(AnimalTameEvent e) {
        handleEvent(e, "animal_tame", (sc) -> {
            sc.setVar("animal", e.getAnimal());
            sc.setVar("tamer", e.getTamer());
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onJump(LivingJumpEvent e) {
        handleEvent(e, "living_jump", (sc) -> {
            setLiving(sc, e.getEntityLiving());
        });
    }
    
    public void onSneak(PlayerEntity p, boolean sneak) {
        handleEvent("player_sneak", (sc) -> {
            setPlayer(sc, p);
            sc.setVar("sneak", sneak);
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onMobGriefing(EntityMobGriefingEvent e) {
        handleEvent(e, "mob_griefing", (sc) -> {
            Entity ent = e.getEntity();
            setEntity(sc, ent);
            boolean b = true;
            if(ent != null) {
                World w = ent.world;
                if(w != null) {
                    GameRules rules = w.getGameRules();
                    if(rules != null) {
                        try {
                            b = !rules.getBoolean(GameRules.MOB_GRIEFING);
                        } catch(Exception ex) {
                            System.out.println("onMobGriefing Exception");
                            ex.printStackTrace();
                        }
                    } else {
                        System.out.println("Rules are null");
                    }
                } else {
                    System.out.println("World is null");
                }
            } else {
                System.out.println("Entity is null");
            }
            setCancel(sc, b);
        }, (sc) -> {
            handleVar(sc, "mob_griefing", "cancel", (v) -> {
                e.setResult(v.getBoolean(sc) ? Result.DENY : Result.ALLOW);
            });
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onWorldLoad(WorldEvent.Load e) {
        handleEvent(e, "world_load", (sc) -> {
            sc.setVar("world", e.getWorld());
        });
    }
    
    @Override
    public void onCraft(int id, World w, PlayerEntity p, CraftingInventory inv,
            CraftResultInventory result) {
        if(w.isRemote) {
            return;
        }
        ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) p;
        Wrapper<ItemStack> wrapper = new Wrapper<>(ItemStack.EMPTY);
        Optional<ICraftingRecipe> optional =
                w.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, w);
        if(optional.isPresent()) {
            ICraftingRecipe icraftingrecipe = optional.get();
            if(result.canUseRecipe(w, serverplayerentity, icraftingrecipe)) {
                wrapper.set(icraftingrecipe.getCraftingResult(inv));
                scripts.getScriptManager().callEvent("craft", (sc) -> {
                    setPlayer(sc, p);
                    sc.setVar("result", wrapper.get());
                }, (sc) -> {
                    handleVar(sc, "craft", "result", (v) -> {
                        ItemStack stack = (ItemStack) v.get(sc);
                        if(stack == null) {
                            wrapper.set(ItemStack.EMPTY);
                        } else {
                            wrapper.set(stack);
                        }
                    });
                });
            }
        }
        result.setInventorySlotContents(0, wrapper.get());
        serverplayerentity.connection.sendPacket(new SSetSlotPacket(id, 0, wrapper.get()));
    }*/

    public static void onVote(String[] data) {
        if(data.length < 5) {
            return;
        }
        //handleEvent("vote", sc -> {
        //    sc.setVar("from", data[1]);
        //    sc.setVar("name", data[2]);
        //    sc.setVar("ip", data[3]);
        //    sc.setVar("timestamp", SnuviUtils.convert(data[4]));
        //});
    }

    /*@Override
    public boolean onClick(Container c, int slot, int dragType, ClickType ct, PlayerEntity p) {
        String name = c.getClass().getSimpleName();
        WrappedBool b = new WrappedBool();
        b.wrapped = false;
        scripts.getScriptManager().callEvent("container_click", sc -> {
            setPlayer(sc, p);
            sc.setVar("slot", (double) slot);
            sc.setVar("item_list", c.getInventory());
            sc.setVar("type", name);
            sc.setVar("drag_type", (double) dragType);
            sc.setVar("click_type", ct.toString());
            setCancel(sc, b.wrapped);
        }, sc -> {
            handleVar(sc, "container_click", "cancel", v -> b.wrapped = v.getBoolean(sc));
        });
        return b.wrapped;
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onEnderTeleport(EnderTeleportEvent e) {
        handleEvent("ender_teleport", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("location", new Location(e.getEntityLiving().getEntityWorld(), e.getTargetX(),
                    e.getTargetY(), e.getTargetZ(), 0.0f, 0.0f));
            setCancel(sc, e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "ender_teleport");
        });
    }*/
}

