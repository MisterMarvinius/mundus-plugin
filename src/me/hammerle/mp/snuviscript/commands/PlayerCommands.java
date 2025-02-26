package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import io.papermc.paper.entity.LookAnchor;
import me.hammerle.mp.MundusPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;

public class PlayerCommands {
    private static HashMap<Integer, String> idToName = null;
    private static HashMap<Integer, UUID> idToUUID = null;

    private static void fillDefaults() {
        if(idToName == null) {
            idToName = new HashMap<>();
            for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                idToName.put(op.getUniqueId().hashCode(), op.getName());
            }
        }
        if(idToUUID == null) {
            idToUUID = new HashMap<>();
            for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                idToUUID.put(op.getUniqueId().hashCode(), op.getUniqueId());
            }
        }
    }

    public static void join(Player p) {
        fillDefaults();
        idToName.put(p.getUniqueId().hashCode(), p.getName());
        idToUUID.put(p.getUniqueId().hashCode(), p.getUniqueId());
    }

    private static String getNameFromId(int id) {
        fillDefaults();
        return idToName.get(id);
    }

    private static UUID getUUIDFromId(int id) {
        fillDefaults();
        return idToUUID.get(id);
    }

    private static int countItemStack(Player p, ItemStack stack) {
        if(stack.getAmount() == 0) {
            return 0;
        }
        int counter = 0;
        ItemStack[] stacks = p.getInventory().getContents();
        for(int i = 0; i < stacks.length; i++) {
            if(stacks[i] != null && stacks[i].isSimilar(stack)) {
                counter += stacks[i].getAmount();
            }
        }
        return counter;
    }

    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("player.getitemamount", (sc,
                in) -> (double) countItemStack((Player) in[0].get(sc), (ItemStack) in[1].get(sc)));
        MundusPlugin.scriptManager.registerFunction("player.removeitem", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            ItemStack stack = (ItemStack) in[1].get(sc);
            int count = countItemStack(p, stack);
            if(count >= stack.getAmount()) {
                p.getInventory().removeItemAnySlot(stack);
                return 0.0;
            }
            return (double) (stack.getAmount() - count);
        });
        MundusPlugin.scriptManager.registerFunction("player.giveitem", (sc, in) -> {
            ItemStack stack = (ItemStack) in[1].get(sc);
            Player p = (Player) in[0].get(sc);
            HashMap<Integer, ItemStack> left = p.getInventory().addItem(stack);
            int count = 0;
            for(ItemStack lStack : left.values()) {
                count += lStack.getAmount();
            }
            return (double) count;
        });
        MundusPlugin.scriptManager.registerFunction("player.additem", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            ItemStack stack = (ItemStack) in[1].get(sc);
            ItemStack[] content = p.getInventory().getContents();
            HashMap<Integer, ItemStack> left = p.getInventory().addItem(stack);
            int count = 0;
            for(ItemStack lStack : left.values()) {
                count += lStack.getAmount();
            }
            if(count != 0) {
                p.getInventory().setContents(content);
            }
            return (double) count;
        });
        MundusPlugin.scriptManager.registerConsumer("player.respawn",
                (sc, in) -> ((Player) in[0].get(sc)).spigot().respawn());
        MundusPlugin.scriptManager.registerConsumer("player.setcompass", (sc, in) -> {
            Player p = ((Player) in[0].get(sc));
            p.setCompassTarget((Location) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.gethunger",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getFoodLevel());
        MundusPlugin.scriptManager.registerConsumer("player.sethunger", (sc, in) -> {
            ((Player) in[0].get(sc)).setFoodLevel(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.getsaturation",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getSaturation());
        MundusPlugin.scriptManager.registerConsumer("player.setsaturation",
                (sc, in) -> ((Player) in[0].get(sc)).setSaturation(in[1].getFloat(sc)));
        MundusPlugin.scriptManager.registerFunction("player.getname", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Player) {
                return ((Player) o).getName();
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(CommandUtils.getUUID(o));
            if(op == null) {
                return null;
            }
            return op.getName();
        });
        MundusPlugin.scriptManager.registerFunction("player.getuuid", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Player) {
                return ((Player) o).getUniqueId();
            }
            OfflinePlayer op = Bukkit.getOfflinePlayerIfCached(o.toString());
            if(op == null) {
                return null;
            }
            return op.getUniqueId();
        });
        MundusPlugin.scriptManager.registerFunction("player.getid",
                (sc, in) -> (double) CommandUtils.getUUID(in[0].get(sc)).hashCode());
        MundusPlugin.scriptManager.registerFunction("player.get",
                (sc, in) -> Bukkit.getPlayer(CommandUtils.getUUID(in[0].get(sc))));
        MundusPlugin.scriptManager.registerFunction("player.getuuidfromid",
                (sc, in) -> getUUIDFromId(in[0].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("player.getnamefromid",
                (sc, in) -> getNameFromId(in[0].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("player.getip",
                (sc, in) -> ((Player) in[0].get(sc)).spigot().getRawAddress().toString());
        MundusPlugin.scriptManager.registerFunction("player.iscreative",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.CREATIVE);
        MundusPlugin.scriptManager.registerFunction("player.isspectator",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.SPECTATOR);
        MundusPlugin.scriptManager.registerFunction("player.issurvival",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.SURVIVAL);
        MundusPlugin.scriptManager.registerFunction("player.isadventure",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.ADVENTURE);
        MundusPlugin.scriptManager.registerConsumer("player.setfly", (sc, in) -> {
            Player p = ((Player) in[0].get(sc));
            boolean b = in[1].getBoolean(sc);
            p.setAllowFlight(b);
        });
        MundusPlugin.scriptManager.registerFunction("player.hasfly",
                (sc, in) -> ((Player) in[0].get(sc)).getAllowFlight());
        MundusPlugin.scriptManager.registerFunction("player.isflying",
                (sc, in) -> ((Player) in[0].get(sc)).isFlying());
        MundusPlugin.scriptManager.registerConsumer("player.setgamemode", (sc, in) -> {
            ((Player) in[0].get(sc)).setGameMode(GameMode.valueOf(in[1].get(sc).toString()));
        });
        MundusPlugin.scriptManager.registerConsumer("player.dropinventory", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Location l = (Location) in[1].get(sc);
            PlayerInventory inv = p.getInventory();
            for(int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if(stack != null) {
                    p.getWorld().dropItemNaturally(l, stack);
                }
            }
            inv.clear();
        });
        MundusPlugin.scriptManager.registerFunction("player.gettargetblock", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            FluidCollisionMode mode = FluidCollisionMode.NEVER;
            if(in.length >= 3 && in[2].getBoolean(sc)) {
                mode = FluidCollisionMode.ALWAYS;
            }
            return p.getTargetBlockExact(in[1].getInt(sc), mode);
        });
        MundusPlugin.scriptManager.registerFunction("player.gettargetentity", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getTargetEntity(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.gettargetcitizen", (sc, in) -> {
            Player player = (Player) in[0].get(sc);
            Entity target = player.getTargetEntity(in[1].getInt(sc));
            if(target != null && CitizensAPI.getNPCRegistry().isNPC(target)) {
                return CitizensAPI.getNPCRegistry().getNPC(target);
            }
            return null;
        });
        MundusPlugin.scriptManager.registerConsumer("player.action", (sc, in) -> {
            Component text = (Component) in[1].get(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, p -> ((Player) p).sendActionBar(text));
        });
        MundusPlugin.scriptManager.registerFunction("player.getspawn", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getRespawnLocation();
        });
        MundusPlugin.scriptManager.registerFunction("player.getlastdeathlocation", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getLastDeathLocation();
        });
        MundusPlugin.scriptManager.registerFunction("player.isblocking", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.isBlocking();
        });
        MundusPlugin.scriptManager.registerFunction("player.getspectatortarget", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getSpectatorTarget();
        });
        MundusPlugin.scriptManager.registerConsumer("player.setspectatortarget", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Entity e = (Entity) in[1].get(sc);
            p.setSpectatorTarget(e);
        });
        MundusPlugin.scriptManager.registerConsumer("player.setsprinting", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            boolean b = in[1].getBoolean(sc);
            p.setSprinting(b);
        });
        MundusPlugin.scriptManager.registerConsumer("player.setsneaking", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            boolean b = in[1].getBoolean(sc);
            p.setSneaking(b);
        });
        MundusPlugin.scriptManager.registerConsumer("player.showelderguardian", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            boolean b = in[1].getBoolean(sc);
            p.showElderGuardian(b);
        });
        MundusPlugin.scriptManager.registerConsumer("player.setspawn", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Location l = (Location) in[1].get(sc);
            p.setRespawnLocation(l, true);
        });
        MundusPlugin.scriptManager.registerConsumer("player.damageitem", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            ItemStack stack = p.getEquipment().getItemInMainHand();
            p.damageItemStack(stack, in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("player.addtotalexp", (sc, in) -> {
            ((Player) in[0].get(sc)).giveExp(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.getlevel",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getLevel());
        MundusPlugin.scriptManager.registerConsumer("player.setlevel", (sc, in) -> {
            ((Player) in[0].get(sc)).setLevel(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.getexp",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getExp());
        MundusPlugin.scriptManager.registerConsumer("player.setexp",
                (sc, in) -> ((Player) in[0].get(sc)).setExp(in[1].getFloat(sc)));
        MundusPlugin.scriptManager.registerConsumer("player.setexpcooldown",
                (sc, in) -> ((Player) in[0].get(sc)).setExpCooldown(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerConsumer("player.sendhurtanimation ",
                (sc, in) -> ((Player) in[0].get(sc)).sendHurtAnimation(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerConsumer("player.lookat", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Entity e = (Entity) in[1].get(sc);
            p.lookAt(e, (LookAnchor) in[2].get(sc), (LookAnchor) in[3].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.gethead", (sc, in) -> {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setPlayerProfile(
                    Bukkit.createProfile(CommandUtils.getUUID(in[0].get(sc)), in[1].getString(sc)));
            skull.setItemMeta(meta);
            return skull;
        });
        MundusPlugin.scriptManager.registerFunction("player.getinv",
                (sc, in) -> ((Player) in[0].get(sc)).getInventory());
        MundusPlugin.scriptManager.registerFunction("player.getenderinv",
                (sc, in) -> ((Player) in[0].get(sc)).getEnderChest());
        MundusPlugin.scriptManager.registerConsumer("player.setdisplayname", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.playerListName((Component) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("player.sendplayerlistheader", (sc, in) -> {
            Component text = (Component) in[1].get(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc,
                    p -> ((Player) p).sendPlayerListHeader(text));
        });
        MundusPlugin.scriptManager.registerConsumer("player.sendplayerlistfooter", (sc, in) -> {
            Component text = (Component) in[1].get(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc,
                    p -> ((Player) p).sendPlayerListFooter(text));
        });
        MundusPlugin.scriptManager.registerConsumer("player.hide", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Player p2 = (Player) in[1].get(sc);
            p.hidePlayer(MundusPlugin.instance, p2);
        });
        MundusPlugin.scriptManager.registerConsumer("player.show", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Player p2 = (Player) in[1].get(sc);
            p.showPlayer(MundusPlugin.instance, p2);
        });
        MundusPlugin.scriptManager.registerFunction("players.getamount",
                (sc, in) -> (double) Bukkit.getOnlinePlayers().size());
        MundusPlugin.scriptManager.registerFunction("players.tolist",
                (sc, in) -> new ArrayList<>(Bukkit.getOnlinePlayers()));
        MundusPlugin.scriptManager.registerFunction("players.near", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getNearbyPlayers(l, in[1].getDouble(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.getnearest", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            double distance = 10.0;
            Collection<Player> players = l.getWorld().getNearbyPlayers(l, distance);
            distance *= distance;
            Player closest = null;
            for(Player p : players) {
                double d = l.distanceSquared(p.getLocation());
                if(d < distance) {
                    closest = p;
                    distance = d;
                }
            }
            return closest;
        });
        MundusPlugin.scriptManager.registerFunction("player.getping", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getPing();
        });
        MundusPlugin.scriptManager.registerConsumer("player.setflyspeed", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setFlySpeed(in[1].getFloat(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("player.setwalkspeed", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setWalkSpeed(in[1].getFloat(sc));
        });
        MundusPlugin.scriptManager.registerFunction("player.issneaking", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.isSneaking();
        });
        MundusPlugin.scriptManager.registerFunction("player.issprinting", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.isSprinting();
        });
        MundusPlugin.scriptManager.registerFunction("player.isblocking", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.isBlocking();
        });
        MundusPlugin.scriptManager.registerConsumer("player.setslot", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.getInventory().setHeldItemSlot(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("player.resetsleep", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 0);
        });
    }
}
