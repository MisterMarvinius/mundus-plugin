package me.hammerle.kp.snuviscript;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;

public class ScriptEvents {
    /*private static class WrappedBool {
        public boolean wrapped;
    }
    
    private static void setLiving(Script sc, LivingEntity ent) {
        sc.setVar("living_entity", ent);
    }
    
    private static void setItem(Script sc, ItemStack stack) {
        sc.setVar("item", stack);
    }
    
    private static void setPlayer(Script sc, PlayerEntity p) {
        sc.setVar("player", p);
    }
    
    @SuppressWarnings("")
    private static void setBlock(Script sc, World w, BlockPos pos, BlockState state) {
        sc.setVar("block_loc", new Location(w, pos));
        sc.setVar("block_type", state.getBlock().getRegistryName().toString());
        sc.setVar("block", state.getBlock());
    }
    
    @SuppressWarnings("")
    private static void setBlock(Script sc, World w, BlockPos pos) {
        BlockState state = w.getBlockState(pos);
        sc.setVar("block_loc", new Location(w, pos));
        sc.setVar("block_type", state.getBlock().getRegistryName().toString());
        sc.setVar("block", state.getBlock());
    }
    
    private static void setEntity(Script sc, Entity ent) {
        sc.setVar("entity", ent);
    }
    
    private static void nothing(Script sc) {}
    
    private void handleEvent(Event e, String event, Consumer<Script> before,
            Consumer<Script> after) {
        if(e.isCancelable()) {
            scripts.getScriptManager().callEvent(event, sc -> {
                before.accept(sc);
                sc.setVar("cancel", e.isCanceled());
            }, sc -> {
                after.accept(sc);
                handleVar(sc, event, "cancel", v -> e.setCanceled(v.getBoolean(sc)));
            });
        } else {
            scripts.getScriptManager().callEvent(event, before, after);
        }
    }
    
    private void handleEvent(Event e, String event, Consumer<Script> before) {
        handleEvent(e, event, before, ScriptEvents::nothing);
    }
    
    private void handleEvent(String event, Consumer<Script> before, Consumer<Script> after) {
        scripts.getScriptManager().callEvent(event, before, after);
    }
    
    private void handleEvent(String event, Consumer<Script> before) {
        handleEvent(event, before, ScriptEvents::nothing);
    }
    
    private void handleVar(Script sc, String event, String name, Consumer<Variable> c) {
        try {
            ifVarNotNull(sc, name, c);
        } catch(Exception ex) {
            scripts.getLogger().print(String.format("invalid var in '%s' event", event), ex, null,
                    sc.getName(), sc, sc.getStackTrace());
        }
    }
    
    private void ifVarNotNull(Script sc, String name, Consumer<Variable> c) {
        Variable v = sc.getVar(name);
        if(v != null) {
            c.accept(v);
        }
    }
    
    private void simpleCancel(Script sc, Event e, String name) {
        try {
            ifVarNotNull(sc, "cancel", v -> e.setCanceled(v.getBoolean(sc)));
        } catch(Exception ex) {
            scripts.getLogger().print(String.format("invalid var in '%s' event", name), ex, null,
                    sc.getName(), sc, sc.getStackTrace());
        }
    }
    
    public void onPlayerDataTick(PlayerEntity p, String var) {
        handleEvent("player_data_tick", sc -> {
            setPlayer(sc, p);
            sc.setVar("var", var);
        });
    }
    
    public void onPlayerStartElytra(PlayerEntity p) {
        handleEvent("player_elytra_start", sc -> {
            setPlayer(sc, p);
        });
    }
    
    public void onPlayerStopElytra(PlayerEntity p) {
        handleEvent("player_elytra_stop", sc -> {
            setPlayer(sc, p);
        });
    }*/

    public static void onPlayerMove(Player p, int id) {
        //handleEvent("player_move", sc -> {
        //    setPlayer(sc, p);
        //    sc.setVar("id", (double) id);
        //});
    }

    /*public boolean onInventoryClick(Script script, ITextComponent text, ModInventory inv, int slot,
            ClickType click, PlayerEntity p) {
        scripts.getScriptManager().callEvent("inv_click", script, sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", inv);
            sc.setVar("inv_id", (double) inv.getModId());
            sc.setVar("inv_name", text.getString());
            sc.setVar("inv_slot", (double) slot);
            sc.setVar("click_type", click.toString());
            setItem(sc, inv.getStackInSlot(slot));
            sc.setVar("cancel", false);
        }, ScriptEvents::nothing);
        Variable v = script.getVar("cancel");
        return v != null && v.getBoolean(script);
    }
    
    public void onInventoryClose(Script script, ITextComponent text, ModInventory inv,
            PlayerEntity p) {
        scripts.getScriptManager().callEvent("inv_close", script, sc -> {
            setPlayer(sc, p);
            sc.setVar("inv", inv);
            sc.setVar("inv_id", (double) inv.getModId());
            sc.setVar("inv_name", text.getString());
        }, ScriptEvents::nothing);
    }
    
    public void onHumanHurt(Entity attacker, EntityHuman h) {
        handleEvent("human_hurt", sc -> {
            setEntity(sc, attacker);
            sc.setVar("human", h);
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onPlayerPostRespawn(PlayerEvent.PlayerRespawnEvent e) {
        handleEvent("player_post_respawn", sc -> setPlayer(sc, e.getPlayer()));
    }
    
    public void onPlayerPreRespawn(PlayerEntity p) {
        handleEvent("player_pre_respawn", sc -> setPlayer(sc, p));
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingDamage(LivingDamageEvent e) {
        handleEvent(e, "living_damage", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("damage_source", e.getSource());
            sc.setVar("damage_amount", (double) e.getAmount());
        }, (sc) -> {
            handleVar(sc, "living_damage", "damage_amount", v -> e.setAmount(v.getFloat(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingHurt(LivingHurtEvent e) {
        if(e.getSource().getDamageType().equals("fireworks")) {
            e.setCanceled(true);
        }
        handleEvent(e, "living_hurt", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("damage_source", e.getSource());
            sc.setVar("damage_amount", (double) e.getAmount());
        }, (sc) -> {
            handleVar(sc, "living_hurt", "damage_amount", v -> e.setAmount(v.getFloat(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingAttacked(LivingAttackEvent e) {
        handleEvent(e, "living_pre_hurt", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("damage_source", e.getSource());
            sc.setVar("damage_amount", (double) e.getAmount());
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingHeal(LivingHealEvent e) {
        handleEvent(e, "living_heal", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("heal_amount", (double) e.getAmount());
        }, (sc) -> {
            handleVar(sc, "living_heal", "heal_amount", v -> e.setAmount(v.getFloat(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingDeath(LivingDeathEvent e) {
        handleEvent(e, "living_death", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("damage_source", e.getSource());
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingDrop(LivingDropsEvent e) {
        handleEvent(e, "living_drop", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("drops", e.getDrops());
            sc.setVar("damage_source", e.getSource());
            sc.setVar("looting", (double) e.getLootingLevel());
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLivingExperienceDrop(LivingExperienceDropEvent e) {
        handleEvent(e, "living_experience_drop", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("experience", (double) e.getDroppedExperience());
        }, (sc) -> {
            handleVar(sc, "living_experience_drop", "experience",
                    v -> e.setDroppedExperience(v.getInt(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onProjectileHit(ProjectileImpactEvent e) {
        final Entity hitEntity;
        final Location loc;
    
        RayTraceResult ray = e.getRayTraceResult();
        switch(ray.getType()) {
            case ENTITY:
                hitEntity = ((EntityRayTraceResult) e.getRayTraceResult()).getEntity();
                loc = null;
                break;
            case BLOCK:
                loc = new Location(e.getEntity().world,
                        ((BlockRayTraceResult) e.getRayTraceResult()).getPos());
                hitEntity = null;
                break;
            default:
                return;
        }
    
        handleEvent(e, "projectile_hit", (sc) -> {
            sc.setVar("projectile", e.getEntity());
            sc.setVar("entity_hit", hitEntity);
            sc.setVar("loc_hit", loc);
            sc.setVar("shooter", Utils.getEntityFromProjectile(e.getEntity()));
        });
    }
    
    public void onEntityItemProjectileHit(EntityItemProjectile ent, LivingEntity liv,
            ItemStack stack, Entity hitEntity, BlockPos pos) {
        Location loc = (pos == null) ? null : new Location(ent.world, pos);
        handleEvent("item_hit", (sc) -> {
            sc.setVar("projectile", ent);
            setItem(sc, stack);
            sc.setVar("entity_hit", hitEntity);
            sc.setVar("loc_hit", loc);
            sc.setVar("shooter", liv);
        });
    }
    
    @Override
    public List<ItemStack> onBlockHarvest(BlockState state, ServerWorld w, BlockPos pos,
            TileEntity tileEnt, Entity ent, ItemStack stack) {
        LootContext.Builder loot = new LootContext.Builder(w).withRandom(w.getRandom())
                .withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos))
                .withParameter(LootParameters.TOOL, stack == null ? ItemStack.EMPTY : stack)
                .withNullableParameter(LootParameters.THIS_ENTITY, ent)
                .withNullableParameter(LootParameters.BLOCK_ENTITY, tileEnt);
        List<ItemStack> list = state.getDrops(loot);
        try {
            final Block b = state.getBlock();
            final String name = b.getRegistryName().toString();
            scripts.getScriptManager().callEvent("block_drop", sc -> {
                sc.setVar("drops", list);
                sc.setVar("block_type", name);
                sc.setVar("block", b);
                sc.setVar("location", new Location(w, pos));
                setEntity(sc, ent);
                setItem(sc, stack);
            }, ScriptEvents::nothing);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        handleEvent("block_break", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setBlock(sc, (World) e.getWorld(), e.getPos(), e.getState());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "block_break");
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if(!(e.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity p = (PlayerEntity) e.getEntity();
        handleEvent("block_place", (sc) -> {
            setPlayer(sc, p);
            sc.setVar("block_type_after", e.getPlacedBlock().getBlock().getRegistryName());
            setBlock(sc, (World) e.getWorld(), e.getPos(), e.getState());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "block_place");
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        PlayerEntity p = e.getPlayer();
        if(p == null) {
            return;
        }
        PlayerList list = server.getPlayerList();
        boolean banned = list.getBannedPlayers().isBanned(p.getGameProfile());
        boolean whitelisted = list.getWhitelistedPlayers().isWhitelisted(p.getGameProfile());
        handleEvent("player_login", (sc) -> {
            setPlayer(sc, p);
            sc.setVar("is_banned", banned);
            sc.setVar("is_whitelisted", whitelisted);
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        PlayerEntity p = e.getPlayer();
        if(p == null || e.getPlayer().ticksExisted < 20) {
            return;
        }
        handleEvent("player_logout", sc -> setPlayer(sc, p));
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onBucketFill(FillBucketEvent e) {
        handleEvent("bucket_use", (sc) -> {
            setPlayer(sc, e.getPlayer());
            RayTraceResult ray = e.getTarget();
            if(ray != null && ray instanceof BlockRayTraceResult
                    && ray.getType() == RayTraceResult.Type.BLOCK) {
                sc.setVar("has_block", true);
                ScriptEvents.setBlock(sc, e.getWorld(), ((BlockRayTraceResult) ray).getPos());
            } else {
                sc.setVar("has_block", false);
            }
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "bucket_use");
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        handleEvent("block_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("action", "right");
            sc.setVar("hand", e.getHand().name());
            ScriptEvents.setBlock(sc, e.getWorld(), e.getPos());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            handleVar(sc, "block_click", "cancel", v -> {
                boolean b = v.getBoolean(sc);
                e.setCanceled(b);
                if(!b) {
                    e.setUseBlock(Result.DEFAULT);
                    e.setUseItem(Result.DEFAULT);
                }
            });
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        handleEvent("block_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("action", "left");
            sc.setVar("hand", e.getHand().name());
            ScriptEvents.setBlock(sc, e.getWorld(), e.getPos());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            handleVar(sc, "block_click", "cancel", v -> {
                boolean b = v.getBoolean(sc);
                e.setCanceled(b);
                if(!b) {
                    e.setUseBlock(Result.DEFAULT);
                    e.setUseItem(Result.DEFAULT);
                }
            });
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onEntityClick(PlayerInteractEvent.EntityInteract e) {
        handleEvent("entity_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("hand", e.getHand().name());
            setEntity(sc, e.getTarget());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "entity_click");
        });
    }
    
    public void onEmptyLeftClick(PlayerEntity p) {
        handleEvent("left_click_air", sc -> setPlayer(sc, p));
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onItemClick(PlayerInteractEvent.RightClickItem e) {
        handleEvent("item_air_click", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setItem(sc, e.getItemStack());
            sc.setVar("hand", e.getHand().toString());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "item_air_click");
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onItemUseStart(LivingEntityUseItemEvent.Start e) {
        handleEvent(e, "item_use_start", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            sc.setVar("duration", (double) e.getDuration());
            setItem(sc, e.getItem());
        }, (sc) -> {
            handleVar(sc, "item_use_start", "duration", v -> e.setDuration(v.getInt(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onConsuming(LivingEntityUseItemEvent.Finish e) {
        handleEvent(e, "item_use_finish", (sc) -> {
            setLiving(sc, e.getEntityLiving());
            setItem(sc, e.getItem());
            sc.setVar("result", e.getResultStack());
        }, (sc) -> {
            handleVar(sc, "item_use_finish", "result", v -> {
                ItemStack stack = (ItemStack) v.get(sc);
                if(stack == null) {
                    e.setResultStack(ItemStack.EMPTY);
                } else {
                    e.setResultStack(stack);
                }
            });
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onFishing(ItemFishedEvent e) {
        handleEvent("fishing", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("drops", e.getDrops());
            sc.setVar("hook", e.getHookEntity());
            sc.setVar("rod_damage", (double) e.getRodDamage());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "fishing");
            handleVar(sc, "fishing", "rod_damage", v -> e.damageRodBy(v.getInt(sc)));
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onCrafting(PlayerEvent.ItemCraftedEvent e) {
        handleEvent("craft", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setItem(sc, e.getCrafting());
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onItemDrop(ItemTossEvent e) {
        handleEvent("player_toss", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setItem(sc, e.getEntityItem().getItem());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "player_toss");
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onItemPickup(EntityItemPickupEvent e) {
        handleEvent("player_pickup", (sc) -> {
            setPlayer(sc, e.getPlayer());
            setEntity(sc, e.getItem());
            setItem(sc, e.getItem().getItem());
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "player_pickup");
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onEntityMount(EntityMountEvent e) {
        Entity ent = e.getEntityBeingMounted();
        if(ent instanceof AbstractHorseEntity) {
            for(PlayerEntity p : ent.getEntityWorld().getPlayers()) {
                if(p.openContainer == null
                        || !(p.openContainer instanceof HorseInventoryContainer)) {
                    continue;
                }
                p.closeScreen();
            }
        }
        handleEvent(e, "entity_mount", (sc) -> {
            sc.setVar("mounting", e.isMounting());
            setEntity(sc, ent);
            sc.setVar("rider", e.getEntityMounting());
        });
    }
    
    @SubscribeEvent(receiveCanceled = true)
    public void onPlayerUsePortal(PlayerEvent.PlayerChangedDimensionEvent e) {
        handleEvent("portal", (sc) -> {
            setPlayer(sc, e.getPlayer());
            sc.setVar("from", e.getFrom().getRegistryName().getPath());
            sc.setVar("to", e.getTo().getRegistryName().getPath());
        });
    }*/

    public static boolean onCommand(Player p, String command) {
        //handleEvent("command", (sc) -> {
        //    setPlayer(sc, e.getPlayer());
        //    sc.setVar("command", e.getName());
        //    sc.setVar("cancel", e.isCanceled());
        //}, (sc) -> {
        //    handleVar(sc, "command", "cancel", v -> e.setCanceled(v.getBoolean(sc)));
        //});
        return false;
    }

    public static void onCustomCommand(Player p, String command, String[] args) {
        KajetansPlugin.log("Custom Command: " + command);
        for(String s : args) {
            KajetansPlugin.log(s);
        }
        //handleEvent("custom_command", (sc) -> {
        //    setPlayer(sc, p);
        //    sc.setVar("command", command);
        //    if(args.length == 0) {
        //        sc.setVar("args", new ArrayList<>());
        //        sc.setVar("text_args", new ArrayList<>());
        //    } else {
        //        sc.setVar("args", Arrays.stream(args).map(s -> SnuviUtils.convert(s))
        //                .collect(Collectors.toList()));
        //        sc.setVar("text_args", Arrays.stream(args).collect(Collectors.toList()));
        //    }
        //});
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
            sc.setVar("cancel", e.isCanceled());
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
            sc.setVar("cancel", b);
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
            sc.setVar("cancel", b.wrapped);
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
            sc.setVar("cancel", e.isCanceled());
        }, (sc) -> {
            simpleCancel(sc, e, "ender_teleport");
        });
    }*/
}

