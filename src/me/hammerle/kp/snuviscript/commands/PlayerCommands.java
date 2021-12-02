/*package me.km.snuviscript.commands;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.km.inventory.InventoryUtils;
import me.km.overrides.ModEntityPlayerMP;
import me.km.permissions.Permissions;
import me.km.playerbank.IPlayerBank;
import me.km.scheduler.SnuviScheduler;
import me.km.snuviscript.Scripts;
import static me.km.snuviscript.commands.CommandUtils.*;
import me.km.utils.Location;
import me.km.utils.ReflectionUtils;
import me.km.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PlayerCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions(ScriptManager sm, Scripts scripts, Permissions perms,
            SnuviScheduler scheduler, MinecraftServer server, IPlayerBank playerBank) {
        sm.registerFunction("player.getitemamount",
                (sc, in) -> (double) InventoryUtils.searchInventoryFor(
                        ((PlayerEntity) in[0].get(sc)).inventory, (ItemStack) in[2].get(sc),
                        in[1].getBoolean(sc)));
        sm.registerFunction("player.removeitem", (sc, in) -> {
            ItemStack stack = ((ItemStack) in[1].get(sc)).copy();
            stack.setCount(InventoryUtils
                    .removeFromInventory(((PlayerEntity) in[0].get(sc)).inventory, stack));
            return stack;
        });
        sm.registerFunction("player.giveitem", (sc, in) -> {
            ItemStack stack = ((ItemStack) in[1].get(sc)).copy();
            stack.setCount(
                    InventoryUtils.addToInventory(((PlayerEntity) in[0].get(sc)).inventory, stack));
            return stack;
        });
        sm.registerConsumer("player.respawn", (sc, in) -> {
            final ServerPlayerEntity p = ((ServerPlayerEntity) in[0].get(sc));
            scheduler.scheduleTask("player.respawn", () -> {
                try {
                    p.connection.processClientStatus(
                            new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
                } catch(ThreadQuickExitException ex) {
                    // Minecraft needs this for canceling and queueing into main thread
                }
            });
        });
        sm.registerConsumer("player.clearinventory",
                (sc, in) -> ((PlayerEntity) in[0].get(sc)).inventory.clear());
        sm.registerFunction("player.inventorytolist",
                (sc, in) -> ((PlayerEntity) in[1].get(sc)).inventory.mainInventory);
        sm.registerFunction("player.getnearest", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getClosestPlayer(l.getX(), l.getY(), l.getZ(), -1, p -> true);
        });
        sm.registerConsumer("player.say", (sc, in) -> {
            try {
                ((ServerPlayerEntity) in[0].get(sc)).connection
                        .processChatMessage(new CChatMessagePacket(SnuviUtils.connect(sc, in, 1)));
            } catch(ThreadQuickExitException ex) {
                // Minecraft needs this for canceling and queueing into main thread
            }
        });
        sm.registerConsumer("player.setcompass", (sc, in) -> {
            ((ServerPlayerEntity) in[0].get(sc)).connection.sendPacket(
                    new SWorldSpawnChangedPacket(((Location) in[1].get(sc)).getBlockPos(),
                            in.length > 2 ? in[2].getFloat(sc) : 0.0f));
        });
        sm.registerFunction("player.gethunger",
                (sc, in) -> (double) ((PlayerEntity) in[0].get(sc)).getFoodStats().getFoodLevel());
        sm.registerConsumer("player.sethunger", (sc, in) -> {
            ((PlayerEntity) in[0].get(sc)).getFoodStats().setFoodLevel(in[1].getInt(sc));
        });
        sm.registerFunction("player.getsaturation", (sc,
                in) -> (double) ((PlayerEntity) in[0].get(sc)).getFoodStats().getSaturationLevel());
        sm.registerConsumer("player.setsaturation", (sc, in) -> {
            ReflectionUtils.setSaturation(((PlayerEntity) in[0].get(sc)).getFoodStats(),
                    in[1].getFloat(sc));
        });
        sm.registerFunction("player.getname", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof PlayerEntity) {
                return ((PlayerEntity) o).getName().getString();
            }
            GameProfile gp = server.getPlayerProfileCache().getProfileByUUID(getUUID(o));
            if(gp == null) {
                return null;
            }
            return gp.getName();
        });
        sm.registerFunction("player.getuuid", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof PlayerEntity) {
                return ((PlayerEntity) o).getUniqueID();
            }
            return playerBank.getUUID(o.toString());
        });
        sm.registerFunction("player.getid", (sc, in) -> (double) getId(playerBank, in[0].get(sc)));
        sm.registerFunction("player.get",
                (sc, in) -> server.getPlayerList().getPlayerByUUID(getUUID(in[0].get(sc))));
        sm.registerFunction("player.getuuidfromid",
                (sc, in) -> playerBank.getUUIDfromID(in[0].getInt(sc)));
        sm.registerFunction("player.getnamefromid",
                (sc, in) -> playerBank.getNamefromID(in[0].getInt(sc)));
        sm.registerFunction("player.getip",
                (sc, in) -> ((ServerPlayerEntity) in[0].get(sc)).connection.netManager
                        .getRemoteAddress().toString());
        sm.registerFunction("player.iscreative",
                (sc, in) -> ((PlayerEntity) in[0].get(sc)).isCreative());
        sm.registerFunction("player.isspectator",
                (sc, in) -> ((PlayerEntity) in[0].get(sc)).isSpectator());
        sm.registerFunction("player.issurvival", (sc, in) -> {
            PlayerEntity p = (PlayerEntity) in[0].get(sc);
            return !p.isCreative() && !p.isSpectator();
        });
        sm.registerFunction("player.isadventure",
                (sc, in) -> !((PlayerEntity) in[0].get(sc)).abilities.allowEdit);
        sm.registerConsumer("player.setfly", (sc, in) -> {
            PlayerEntity p = ((PlayerEntity) in[0].get(sc));
            boolean b = in[1].getBoolean(sc);
            p.abilities.allowFlying = b;
            p.abilities.isFlying = b;
            p.sendPlayerAbilities();
        });
        sm.registerFunction("player.hasfly",
                (sc, in) -> ((PlayerEntity) in[0].get(sc)).abilities.allowFlying);
        sm.registerFunction("player.isflying",
                (sc, in) -> ((PlayerEntity) in[0].get(sc)).abilities.isFlying);
        sm.registerConsumer("player.setgamemode", (sc, in) -> {
            PlayerEntity p = (PlayerEntity) in[0].get(sc);
            switch(in[1].get(sc).toString()) {
                case "survival":
                case "s":
                case "0":
                    p.setGameType(GameType.SURVIVAL);
                    return;
                case "creative":
                case "c":
                case "1":
                    p.setGameType(GameType.CREATIVE);
                    return;
                case "adventure":
                case "a":
                case "2":
                    p.setGameType(GameType.ADVENTURE);
                    return;
                case "spectator":
                case "w":
                case "3":
                    p.setGameType(GameType.SPECTATOR);
                    return;
            }
            p.setGameType(GameType.CREATIVE);
        });
        sm.registerFunction("player.getlastdamager", (sc, in) -> {
            DamageSource ds = ((PlayerEntity) in[0].get(sc)).getLastDamageSource();
            if(ds == null) {
                return null;
            }
            return ds.getImmediateSource();
        });
        sm.registerConsumer("player.dropinventory", (sc, in) -> {
            ((PlayerEntity) in[0].get(sc)).inventory.dropAllItems();
        });
        sm.registerFunction("player.gettarget", (sc, in) -> {
            PlayerEntity p = (PlayerEntity) in[0].get(sc);

            double radius = in[1].getDouble(sc);
            if(radius > 128.0) {
                radius = 128.0;
            }

            RayTraceContext.BlockMode bm = RayTraceContext.BlockMode.OUTLINE;
            if(in.length >= 3 && in[2].getBoolean(sc)) {
                bm = RayTraceContext.BlockMode.COLLIDER;
            }
            RayTraceContext.FluidMode fm = RayTraceContext.FluidMode.NONE;
            if(in.length >= 4 && in[3].getBoolean(sc)) {
                fm = RayTraceContext.FluidMode.ANY;
            }

            Vector3d start = p.getEyePosition(0.0f);
            Vector3d unit = p.getLook(0.0f);
            Vector3d end = start.add(unit.x * radius, unit.y * radius, unit.z * radius);

            BlockRayTraceResult result =
                    p.world.rayTraceBlocks(new RayTraceContext(start, end, bm, fm, p));

            if(result.getType() == RayTraceResult.Type.BLOCK) {
                return new Location(p.world, result.getPos());
            }

            return new Location(p.world, end);
        });
        sm.registerFunction("player.gettargetentity", (sc, in) -> {
            return Utils.getTargetedEntity((PlayerEntity) in[0].get(sc), in[1].getDouble(sc),
                    (Class<? extends Entity>) getNamedClass(in[2].getString(sc)));
        });
        sm.registerAlias("player.hasscript", "player.hasquest");
        sm.registerConsumer("player.action", (sc, in) -> {
            StringTextComponent text = new StringTextComponent(SnuviUtils.connect(sc, in, 1));
            doForGroup(server, scripts, perms, in[0].get(sc), sc,
                    p -> ((ServerPlayerEntity) p).sendStatusMessage(text, true));
        });
        sm.registerConsumer("player.disconnect", (sc, in) -> {
            ((ServerPlayerEntity) in[0].get(sc)).connection
                    .disconnect(new StringTextComponent(in[1].getString(sc)));
        });
        sm.registerFunction("player.getspawn", (sc, in) -> {
            ServerPlayerEntity p = (ServerPlayerEntity) in[0].get(sc);
            return new Location(p.world, p.func_241140_K_());
        });
        sm.registerAlias("player.getspawn", "player.getbedspawn");
        sm.registerConsumer("player.setspawn", (sc, in) -> {
            Location l = (Location) in[1].get(sc);
            ((ServerPlayerEntity) in[0].get(sc)).func_242111_a(
                    ((ServerWorld) l.getWorld()).getDimensionKey(), l.getBlockPos(),
                    in.length > 2 ? in[2].getFloat(sc) : 0.0f, true, false);
        });
        sm.registerAlias("player.setspawn", "player.setbedspawn");
        sm.registerConsumer("player.damageitem", (sc, in) -> {
            PlayerEntity p = (PlayerEntity) in[0].get(sc);
            p.getHeldItemMainhand().damageItem(in[1].getInt(sc), p, (c) -> {
            });
        });
        sm.registerConsumer("player.damagearmor", (sc, in) -> {
            ((PlayerEntity) in[0].get(sc)).inventory.func_234563_a_((DamageSource) in[2].get(sc),
                    in[1].getFloat(sc));
        });
        sm.registerConsumer("player.openenderchest", (sc, in) -> {
            PlayerEntity p1 = (PlayerEntity) in[0].get(sc);
            PlayerEntity p2 = (PlayerEntity) in[1].get(sc);
            EnderChestInventory inv = p2.getInventoryEnderChest();
            p1.openContainer(new SimpleNamedContainerProvider((id, pInv, p) -> {
                return ChestContainer.createGeneric9X3(id, pInv, inv);
            }, new StringTextComponent(in[2].getString(sc))));
        });
        sm.registerConsumer("player.addtotalexp", (sc, in) -> {
            ((ServerPlayerEntity) in[0].get(sc)).giveExperiencePoints(in[1].getInt(sc));
        });
        sm.registerFunction("player.getlevel",
                (sc, in) -> (double) ((PlayerEntity) in[0].get(sc)).experienceLevel);
        sm.registerConsumer("player.setlevel", (sc, in) -> {
            ((ServerPlayerEntity) in[0].get(sc)).setExperienceLevel(in[1].getInt(sc));
        });
        sm.registerFunction("player.getexp",
                (sc, in) -> (double) ((PlayerEntity) in[0].get(sc)).experience);
        sm.registerConsumer("player.setexp", (sc, in) -> {
            ServerPlayerEntity p = (ServerPlayerEntity) in[0].get(sc);
            p.func_195394_a((int) (in[1].getDouble(sc) * p.xpBarCap()));
        });
        sm.registerFunction("player.gethead", (sc, in) -> {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            CompoundNBT com = stack.getOrCreateTag();
            GameProfile gp = new GameProfile(getUUID(in[0].get(sc)), in[1].getString(sc));
            gp = SkullTileEntity.updateGameProfile(gp);
            com.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gp));
            return stack;
        });
        sm.registerFunction("player.near",
                (sc, in) -> Utils.getPlayers((Entity) in[0].get(sc), in[1].getDouble(sc)));
        sm.registerFunction("player.getinv", (sc, in) -> ((PlayerEntity) in[0].get(sc)).inventory);
        sm.registerFunction("player.getenderinv",
                (sc, in) -> ((PlayerEntity) in[0].get(sc)).getInventoryEnderChest());
        sm.registerConsumer("player.setdisplayname", (sc, in) -> {
            ((ModEntityPlayerMP) in[0].get(sc)).setTabListDisplayName(in[1].getString(sc),
                    scheduler);
        });
        sm.registerConsumer("player.hide", (sc, in) -> {
            ServerPlayerEntity p = (ServerPlayerEntity) in[0].get(sc);
            GameType type = p.interactionManager.getGameType();
            ReflectionUtils.setGameType(p.interactionManager, GameType.SPECTATOR);
            SPlayerListItemPacket packet =
                    new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_GAME_MODE, p);
            ReflectionUtils.setGameType(p.interactionManager, type);
            for(ServerPlayerEntity other : server.getPlayerList().getPlayers()) {
                if(other == p) {
                    continue;
                }
                other.connection.sendPacket(packet);
            }
            p.setInvisible(true);
        });
        sm.registerConsumer("player.show", (sc, in) -> {
            ServerPlayerEntity p = (ServerPlayerEntity) in[0].get(sc);
            p.setInvisible(false);
            SPlayerListItemPacket packet =
                    new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_GAME_MODE, p);
            for(ServerPlayerEntity other : server.getPlayerList().getPlayers()) {
                if(other == p) {
                    continue;
                }
                other.connection.sendPacket(packet);
            }
        });
        sm.registerFunction("players.getamount",
                (sc, in) -> (double) server.getCurrentPlayerCount());
        sm.registerFunction("players.tolist",
                (sc, in) -> new ArrayList<>(server.getPlayerList().getPlayers()));
        sm.registerFunction("players.toworldlist",
                (sc, in) -> new ArrayList<>(((World) in[0].get(sc)).getPlayers()));
        sm.registerFunction("players.near", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return Utils.getPlayers(l.getWorld(), l.getX(), l.getY(), l.getZ(),
                    in[1].getDouble(sc));
        });
    }
}
*/
